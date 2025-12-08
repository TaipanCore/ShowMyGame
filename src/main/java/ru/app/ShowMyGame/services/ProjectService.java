package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.repositories.ProjectRepository;

import java.io.IOException;
import java.time.LocalDate;
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
        return projectRepository.getProjectById(id).orElseThrow(() -> new RuntimeException("Проект не найден"));
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
            String fileName = fileService.storeImage(imageFile, savedProject);
            savedProject.setImageFileName(fileName);
        }
        if (buildZip != null && !buildZip.isEmpty())
        {

            String folderName = fileService.storeBuild(buildZip, savedProject);
            savedProject.setBuildFolderName(folderName);
            savedProject.setBuildType("web");
        }
        return projectRepository.editProject(savedProject);
    }
    public Project editProject(Project project, MultipartFile newImageFile, MultipartFile newBuildZip) throws IOException
    {
        Project existProject = projectRepository.getProjectById(project.getId()).orElseThrow(() -> new RuntimeException("Проект не найден"));
        existProject.setTitle(project.getTitle());
        existProject.setDescription(project.getDescription());
        existProject.setGenres(project.getGenres());
        existProject.setTags(project.getTags());
        if (newImageFile != null && !newImageFile.isEmpty())
        {
            fileService.deleteImage(project);
            String newFileName = fileService.storeImage(newImageFile, project);
            existProject.setImageFileName(newFileName);
        }
        if (newBuildZip != null && !newBuildZip.isEmpty())
        {
            fileService.deleteBuild(project);
            String newFolderName = fileService.storeBuild(newBuildZip, project);
            existProject.setBuildFolderName(newFolderName);
        }
        return projectRepository.editProject(existProject);
    }
    public void deleteProjectById(Integer id) throws IOException
    {
        Project project = projectRepository.getProjectById(id).orElseThrow(() -> new RuntimeException("Проект не найден"));
        fileService.deleteProjectFiles(project);
        projectRepository.deleteProject(project);
    }
}
