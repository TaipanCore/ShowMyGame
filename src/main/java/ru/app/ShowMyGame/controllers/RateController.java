package ru.app.ShowMyGame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.app.ShowMyGame.entities.Rate;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.RateService;
import ru.app.ShowMyGame.services.ProjectService;
import ru.app.ShowMyGame.helpers.SessionHelper;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rates")
public class RateController
{

    @Autowired
    private RateService rateService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SessionHelper sessionHelper;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> rateProject(@PathVariable Integer projectId, @RequestParam Integer rate, HttpServletRequest request)
    {
        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }

            Project project = projectService.getProjectById(projectId);
            Rate newRate = rateService.getRateByProjectAndUser(project, currentUser).orElse(null);
            if (newRate != null)
            {
                rateService.updateRate(newRate.getId(), rate);
            }
            else
            {
                newRate = rateService.rateProject(project, currentUser, rate);
            }
            Double averageRate = rateService.getAverageRateForProject(project);

            return ResponseEntity.ok(Map.of("success", true, "message", "Оценка поставлена", "rate", Map.of("value", newRate.getRate(), "createdAt", newRate.getCreatedAt()), "averageRate", averageRate, "totalRates", rateService.getRateCountForProject(project)));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/project/{projectId}/my")
    public ResponseEntity<?> getMyRateForProject(@PathVariable Integer projectId, HttpServletRequest request)
    {
        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.ok(Map.of("success", true, "hasRate", false));
            }
            Project project = projectService.getProjectById(projectId);
            Optional<Rate> rate = rateService.getRateByProjectAndUser(project, currentUser);

            if (rate.isPresent())
            {
                return ResponseEntity.ok(Map.of("success", true, "hasRate", true, "rate", Map.of("id", rate.get().getId(), "value", rate.get().getRate(), "createdAt", rate.get().getCreatedAt())));
            }
            else
            {
                return ResponseEntity.ok(Map.of("success", true, "hasRate", false));
            }
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/project/{projectId}/average")
    public ResponseEntity<?> getAverageRate(@PathVariable Integer projectId)
    {
        try
        {
            Project project = projectService.getProjectById(projectId);
            Double averageRate = rateService.getAverageRateForProject(project);
            int totalRates = rateService.getRateCountForProject(project);

            return ResponseEntity.ok(Map.of("success", true, "averageRate", averageRate, "totalRates", totalRates));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PutMapping("/{rateId}")
    public ResponseEntity<?> updateRate(@PathVariable Integer rateId, @RequestParam Integer rate, HttpServletRequest request)
    {
        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }

            Rate updatedRate = rateService.updateRate(rateId, rate);
            Double averageRate = rateService.getAverageRateForProject(updatedRate.getProject());
            return ResponseEntity.ok(Map.of("success", true, "message", "Оценка обновлена", "rate", Map.of("id", updatedRate.getId(), "value", updatedRate.getRate()), "averageRate", averageRate));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{rateId}")
    public ResponseEntity<?> deleteRate(@PathVariable Integer rateId, HttpServletRequest request)
    {

        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }
            Rate rate = rateService.getRateById(rateId);
            Project project = rate.getProject();
            rateService.deleteRate(rateId);
            Double averageRate = rateService.getAverageRateForProject(project);
            return ResponseEntity.ok(Map.of("success", true, "message", "Оценка удалена", "averageRate", averageRate, "totalRates", rateService.getRateCountForProject(project)));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/project/{projectId}/all")
    public ResponseEntity<?> getProjectRates(@PathVariable Integer projectId)
    {
        try
        {
            Project project = projectService.getProjectById(projectId);
            List<Rate> rates = rateService.getRatesByProject(project);
            return ResponseEntity.ok(Map.of("success", true, "rates", rates, "count", rates.size()));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}