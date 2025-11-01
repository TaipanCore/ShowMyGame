package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.repositories.ProjectRepository;

import java.util.List;

@Service
public class ProjectService
{
    @Autowired
    private ProjectRepository projectRepository;

    public Project getProjectById(Integer id)
    {
        return projectRepository.getProjectById(id);
    }
    public List<Project> getAllProjects()
    {
        return projectRepository.getAllProjects();
    }
    public Project addNewProject(Project project)
    {
        return projectRepository.addNewProject(project);
    }
    public Project editProject(Project project)
    {
        return projectRepository.editProject(project);
    }
    public void deleteProjectById(Integer id)
    {
        Project project = projectRepository.getProjectById(id);
        if (project != null)
        {
            projectRepository.deleteProjectById(project);
        }
    }
}
