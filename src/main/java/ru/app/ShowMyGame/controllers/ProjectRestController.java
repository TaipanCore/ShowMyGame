package ru.app.ShowMyGame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.services.ProjectService;

import java.util.List;

@RestController
public class ProjectRestController
{
    @Autowired
    private ProjectService projectService;

    @GetMapping("/project/{id}")
    public Project getProjectById(@PathVariable("id") Integer id)
    {
        return projectService.getProjectById(id);
    }
    @GetMapping("/projects")
    public List<Project> getAllProjects()
    {
        return projectService.getAllProjects();
    }
    @PostMapping("/project")
    public Project addNewProject(@RequestBody Project project)
    {
        return projectService.addNewProject(project);
    }
    @PutMapping("/project")
    public Project editProject(@RequestBody Project project)
    {
        return projectService.editProject(project);
    }
    @DeleteMapping("/task/{id}")
    public String deleteProjectById(@PathVariable("id") Integer id)
    {
        projectService.deleteProjectById(id);
        return "Successfully deleted with id:" + id;
    }
}
