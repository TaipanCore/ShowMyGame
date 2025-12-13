package ru.app.ShowMyGame.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.app.ShowMyGame.entities.*;
import ru.app.ShowMyGame.services.*;
import ru.app.ShowMyGame.helpers.SessionHelper;

import java.io.IOException;
import java.util.*;

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
    private BookmarkService bookmarkService;
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
        List<Rate> totalRates = rateService.getRatesByProject(project);
        Double averageRate = rateService.getAverageRateForProject(project);
        Optional<Rate> userRate = Optional.empty();
        if (currentUser != null)
        {
            userRate = rateService.getRateByProjectAndUser(project, currentUser);
        }
        boolean isBookmarked = bookmarkService.isBookmarked(id, currentUser);
        model.addAttribute("project", project);
        model.addAttribute("comments", comments);
        model.addAttribute("averageRate", averageRate != null ? averageRate : 0.0);
        model.addAttribute("userRate", userRate.orElse(null));
        model.addAttribute("totalRates", totalRates.size());
        model.addAttribute("commentsCount", comments.size());
        model.addAttribute("isBookmarked", isBookmarked);
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
    public String editProjectForm(@PathVariable Integer id, Model model) {
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        model.addAttribute("selectedGenres", project.getGenres());
        model.addAttribute("title", project.getTitle());
        model.addAttribute("description", project.getDescription());

        List<String> tagsList = project.getTags();
        String tagsString = "";
        if (tagsList != null && !tagsList.isEmpty())
        {
            tagsString = String.join(",", tagsList);
        }
        model.addAttribute("selectedTags", tagsString);

        return "edit-project";
    }

    @PostMapping("/project/save")
    public String saveProject(@RequestParam String title, @RequestParam String description, @RequestParam String[] genres, @RequestParam(required = false) String tags, @RequestParam MultipartFile imageFile, @RequestParam MultipartFile buildFile, RedirectAttributes redirectAttributes)
    {
        try
        {
            ArrayList<String> tagsList = new ArrayList<>();
            if (tags != null && !tags.trim().isEmpty() && !tags.trim().equals("[]"))
            {
                String tagsTrimmed = tags.trim();

                if (tagsTrimmed.startsWith("[") && tagsTrimmed.endsWith("]"))
                {
                    try
                    {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> jsonTags = mapper.readValue(tagsTrimmed, new TypeReference<List<String>>() {});
                        tagsList.addAll(jsonTags);
                    }
                    catch (Exception e)
                    {
                        String inner = tagsTrimmed.substring(1, tagsTrimmed.length() - 1);
                        if (!inner.isEmpty())
                        {
                            String[] parts = inner.split(",");
                            for (String part : parts)
                            {
                                String tag = part.trim().replaceAll("^\"|\"$", "").replaceAll("^'|'$", "");
                                if (!tag.isEmpty())
                                {
                                    tagsList.add(tag);
                                }
                            }
                        }
                    }
                }
                else
                {
                    String[] tagArray = tagsTrimmed.split(",");
                    for (String tag : tagArray)
                    {
                        String trimmedTag = tag.trim();
                        if (!trimmedTag.isEmpty())
                        {
                            tagsList.add(trimmedTag);
                        }
                    }
                }
            }

            Project project = new Project();
            project.setTitle(title.trim());
            project.setDescription(description.trim());
            project.setGenres(new ArrayList<>(Arrays.asList(genres)));
            project.setTags(tagsList);
            project.setAuthor(sessionHelper.getCurrentUser());
            Project savedProject = projectService.addNewProject(project, imageFile, buildFile);

            redirectAttributes.addFlashAttribute("success", "Проект успешно создан!");
            return "redirect:/project/" + savedProject.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("selectedGenres", genres != null ? Arrays.asList(genres) : new ArrayList<>());
            redirectAttributes.addFlashAttribute("selectedTags", tags != null ? tags : "");
            return "redirect:/project/create";
        }
    }

    @PostMapping("/project/{id}/update")
    public String updateProject(@PathVariable Integer id, @RequestParam String title, @RequestParam String description, @RequestParam String[] genres, @RequestParam(required = false) String tags, @RequestParam(required = false) MultipartFile imageFile, @RequestParam(required = false) MultipartFile buildFile, RedirectAttributes redirectAttributes) {
        try
        {
            Project project = projectService.getProjectById(id);
            project.setTitle(title.trim());
            project.setDescription(description.trim());
            project.setGenres(new ArrayList<>(Arrays.asList(genres)));

            ArrayList<String> tagsList = new ArrayList<>();
            if (tags != null && !tags.trim().isEmpty() && !tags.trim().equals("[]")) {
                String tagsTrimmed = tags.trim();

                if (tagsTrimmed.startsWith("[") && tagsTrimmed.endsWith("]")) {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        List<String> jsonTags = mapper.readValue(tagsTrimmed, new TypeReference<List<String>>() {});
                        tagsList.addAll(jsonTags);
                    } catch (Exception e) {
                        String inner = tagsTrimmed.substring(1, tagsTrimmed.length() - 1);
                        if (!inner.isEmpty()) {
                            String[] parts = inner.split(",");
                            for (String part : parts) {
                                String tag = part.trim().replaceAll("^\"|\"$", "").replaceAll("^'|'$", "");
                                if (!tag.isEmpty()) {
                                    tagsList.add(tag);
                                }
                            }
                        }
                    }
                }
                else {
                    String[] tagArray = tagsTrimmed.split(",");
                    for (String tag : tagArray) {
                        String trimmedTag = tag.trim();
                        if (!trimmedTag.isEmpty()) {
                            tagsList.add(trimmedTag);
                        }
                    }
                }
            }

            project.setTags(tagsList);
            Project updated = projectService.editProject(project, imageFile, buildFile);
            redirectAttributes.addFlashAttribute("success", "Проект успешно обновлен!");
            return "redirect:/project/" + updated.getId();

        } catch (Exception e) {
            System.err.println("Ошибка при обновлении: " + e.getMessage());
            e.printStackTrace();

            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            redirectAttributes.addFlashAttribute("title", title);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("selectedGenres", genres != null ? Arrays.asList(genres) : new ArrayList<>());
            redirectAttributes.addFlashAttribute("selectedTags", tags != null ? tags : "");  // ← Пустая строка вместо "[]"
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
