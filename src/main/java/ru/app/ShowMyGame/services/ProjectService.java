package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.repositories.ProjectRepository;

import java.io.IOException;
import java.util.List;

@Service
public class ProjectService
{
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private FileService fileService;

    public Project getProjectById(Integer id)
    {
        return projectRepository.getProjectById(id);
    }
    public List<Project> getAllProjects()
    {
        return projectRepository.getAllProjects();
    }
    public Project addNewProject(Project project, MultipartFile imageFile, MultipartFile buildZip) throws IOException
    {
        Project savedProject = projectRepository.addNewProject(project);
        if (imageFile != null && !imageFile.isEmpty())
        {
            savedProject.setImageFileName(imageFile.getOriginalFilename());
            fileService.storeImage(imageFile, savedProject);
        }
        if (buildZip != null && !buildZip.isEmpty())
        {
            savedProject.setBuildFileName(fileService.getBuildFolderName(buildZip));
            fileService.storeBuild(buildZip, savedProject);
            savedProject.setBuildType("web");
        }
        return projectRepository.editProject(savedProject);
    }
    public Project editProject(Project project, MultipartFile newImageFile, MultipartFile newBuildZip) throws IOException
    {
        Project existProject = projectRepository.getProjectById(project.getId());
        if (existProject == null)
        {
            throw new RuntimeException("Проект не найден!");
        }
        existProject.setTitle(project.getTitle());
        existProject.setDescription(project.getDescription());
        existProject.setGenre(project.getGenre());
        existProject.setTags(project.getTags());
        if (newImageFile != null && !newImageFile.isEmpty())
        {
            String newImageName = fileService.storeImage(newImageFile, project);
            existProject.setImageFileName(newImageName);
        }
        if (newBuildZip != null && !newBuildZip.isEmpty())
        {
            fileService.deleteBuild(project);
            String newBuildPath = fileService.storeBuild(newBuildZip, project);
            existProject.setBuildFileName(newBuildPath);
        }
        return projectRepository.editProject(existProject);
    }
    public void deleteProjectById(Integer id) throws IOException
    {
        Project project = projectRepository.getProjectById(id);
        if (project != null)
        {
            fileService.deleteProjectFolder(project);
            projectRepository.deleteProject(project);
        }
        else
        {
            throw new RuntimeException("Проект не найден!");
        }
    }
}
