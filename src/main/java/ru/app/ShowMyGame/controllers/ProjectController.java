package ru.app.ShowMyGame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.services.FileService;
import ru.app.ShowMyGame.services.ProjectService;

import java.io.IOException;
import java.util.List;

@Controller
public class ProjectController
{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private FileService fileService;

    @GetMapping("/")
    public String homePage(Model model)
    {
        model.addAttribute("projects", projectService.getAllProjects());
        return "index";
    }
    @GetMapping("/game/{id}")
    public String projectPage(@PathVariable Integer id, Model model)
    {
        Project project = projectService.getProjectById(id);
        if (project == null) return "error/404";

        model.addAttribute("project", project);
        if ("web".equals(project.getBuildType()))
        {
            model.addAttribute("hasGame", fileService.hasIndexPage(project));
        }
        return "project-details";
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
