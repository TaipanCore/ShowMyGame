package ru.app.ShowMyGame.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.app.ShowMyGame.entities.Comment;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.Rate;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.CommentService;
import ru.app.ShowMyGame.services.FileService;
import ru.app.ShowMyGame.services.ProjectService;
import ru.app.ShowMyGame.helpers.SessionHelper;
import ru.app.ShowMyGame.services.RateService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class ProjectController
{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RateService rateService;
    @Autowired
    private SessionHelper sessionHelper;

    @GetMapping("/projects")
    public String homePage(Model model)
    {
        model.addAttribute("projects", projectService.getAllProjects());
        return "main-page";
    }
    @GetMapping("/project/{id}")
    public String projectPage(@PathVariable Integer id, Model model)
    {
        Project project = projectService.getProjectById(id);
        User currentUser = sessionHelper.getCurrentUser();
        List<Comment> comments = commentService.getCommentsByProject(project);
        Double averageRate = rateService.getAverageRateForProject(project);
        Optional<Rate> userRate = Optional.empty();
        if (currentUser != null)
        {
            userRate = rateService.getUserRateForProject(project, currentUser);
        }
        model.addAttribute("project", project);
        model.addAttribute("comments", comments);
        model.addAttribute("averageRate", averageRate != null ? averageRate : 0.0);
        model.addAttribute("userRate", userRate.orElse(null));
        model.addAttribute("commentsCount", comments.size());
        if ("web".equals(project.getBuildType()))
        {
            model.addAttribute("hasGame", fileService.hasIndexPage(project));
        }
        return "project-page";
    }

    @GetMapping("/project/create")
    public String createProjectForm(Model model)
    {
        if (!model.containsAttribute("title"))
        {
            model.addAttribute("title", "");
        }
        if (!model.containsAttribute("description"))
        {
            model.addAttribute("description", "");
        }
        if (!model.containsAttribute("selectedGenres"))
        {
            model.addAttribute("selectedGenres", new ArrayList<String>());
        }
        if (!model.containsAttribute("selectedTags"))
        {
            model.addAttribute("selectedTags", "[]");
        }
        return "create-project";
    }

    @GetMapping("/project/{id}/edit")
    public String editProjectForm(@PathVariable Integer id, Model model)
    {
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        model.addAttribute("selectedGenres", project.getGenres());
        model.addAttribute("title", project.getTitle());
        model.addAttribute("description", project.getDescription());
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String tagsJson = mapper.writeValueAsString(project.getTags());
            model.addAttribute("selectedTags", tagsJson);
        }
        catch (Exception e)
        {
            model.addAttribute("selectedTags", "[]");
        }
        return "edit-project";
    }

    @PostMapping("/project/save")
    public String saveProject(@RequestParam String title, @RequestParam String description, @RequestParam String[] genres, @RequestParam(required = false) String tagsJson, @RequestParam MultipartFile imageFile, @RequestParam MultipartFile buildFile, RedirectAttributes redirectAttributes)
    {
        try
        {
            List<String> tagsList = new ArrayList<>();
            if (tagsJson != null && !tagsJson.isEmpty())
            {
                try
                {
                    ObjectMapper mapper = new ObjectMapper();
                    tagsList = mapper.readValue(tagsJson, new TypeReference<List<String>>() {});

                    if (tagsList.size() > 10)
                    {
                        throw new RuntimeException("Можно добавить не более 10 тегов");
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Ошибка при обработке тегов: " + e.getMessage());
                }
            }
            Project project = new Project();
            project.setTitle(title.trim());
            project.setDescription(description.trim());
            project.setGenres(new ArrayList<>(Arrays.asList(genres)));
            project.setTags(new ArrayList<>(tagsList));
            project.setAuthor(sessionHelper.getCurrentUser());
            Project savedProject = projectService.addNewProject(project, imageFile, buildFile);

            redirectAttributes.addFlashAttribute("success", "Проект успешно создан!");
            return "redirect:/project/" + savedProject.getId();

        }
        catch (Exception e)
        {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("selectedGenres", genres != null ? Arrays.asList(genres) : new ArrayList<>());
            if (tagsJson != null)
            {
                redirectAttributes.addFlashAttribute("selectedTags", tagsJson);
            }
            else
            {
                redirectAttributes.addFlashAttribute("selectedTags", "[]");
            }
            return "redirect:/project/create";
        }
    }

    @PostMapping("/project/{id}/update")
    public String updateProject(@PathVariable Integer id, @RequestParam String title, @RequestParam String description, @RequestParam String[] genres, @RequestParam(required = false) String tagsJson, @RequestParam(required = false) MultipartFile imageFile, @RequestParam(required = false) MultipartFile buildFile, RedirectAttributes redirectAttributes)
    {
        try
        {
            List<String> tagsList = new ArrayList<>();
            if (tagsJson != null && !tagsJson.isEmpty())
            {
                try
                {
                    ObjectMapper mapper = new ObjectMapper();
                    tagsList = mapper.readValue(tagsJson, new TypeReference<List<String>>() {});

                    if (tagsList.size() > 10)
                    {
                        throw new RuntimeException("Можно добавить не более 10 тегов");
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException("Ошибка при обработке тегов: " + e.getMessage());
                }
            }
            Project project = projectService.getProjectById(id);

            project.setTitle(title.trim());
            project.setDescription(description.trim());
            project.setGenres(new ArrayList<>(Arrays.asList(genres)));
            project.setTags(new ArrayList<>(tagsList));
            Project updated = projectService.editProject(project, imageFile, buildFile);

            redirectAttributes.addFlashAttribute("success", "Проект успешно обновлен!");
            return "redirect:/project/" + updated.getId();

        }
        catch (Exception e)
        {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("selectedGenres", genres != null ? Arrays.asList(genres) : new ArrayList<>());
            if
            (tagsJson != null)
            {
                redirectAttributes.addFlashAttribute("selectedTags", tagsJson);
            }
            else
            {
                redirectAttributes.addFlashAttribute("selectedTags", "[]");
            }
            return "redirect:/project/" + id + "/edit";
        }
    }

    @PostMapping("/project/{id}/delete")
    public String deleteProject(@PathVariable Integer id, RedirectAttributes redirectAttributes)
    {
        try
        {
            projectService.deleteProjectById(id);
            redirectAttributes.addFlashAttribute("success", "Проект успешно удален!");
            return "redirect:/projects";
        }
        catch (Exception e)
        {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении: " + e.getMessage());
            return "redirect:/project/" + id;
        }
    }

    @GetMapping("/api/project/{id}")
    @ResponseBody
    public Project getProjectById(@PathVariable("id") Integer id)
    {
        return projectService.getProjectById(id);
    }

    @GetMapping("/api/projects")
    @ResponseBody
    public List<Project> getAllProjects()
    {
        return projectService.getAllProjects();
    }

    @PostMapping("/api/project")
    @ResponseBody
    public Project addNewProject(@RequestPart("project") Project project, @RequestPart(value = "imageFile", required = false) MultipartFile imageFile, @RequestPart(value = "buildFile", required = false) MultipartFile buildFile) throws IOException
    {
        return projectService.addNewProject(project, imageFile, buildFile);
    }

    @PutMapping("/api/project")
    @ResponseBody
    public Project editProject(@RequestPart("project") Project project, @RequestPart(value = "imageFile", required = false) MultipartFile imageFile, @RequestPart(value = "buildFile", required = false) MultipartFile buildFile) throws IOException
    {
        return projectService.editProject(project, imageFile, buildFile);
    }

    @DeleteMapping("/api/project/{id}")
    @ResponseBody
    public String deleteProjectById(@PathVariable("id") Integer id) throws IOException
    {
        projectService.deleteProjectById(id);
        return "Удален проект с id:" + id;
    }
}
