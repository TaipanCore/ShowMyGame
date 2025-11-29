package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.utils.FilesHelper;
import ru.app.ShowMyGame.utils.PathHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileService
{
    @Autowired
    private PathHelper pathHelper;
    @Autowired
    private FilesHelper filesHelper;

    public String storeImage(MultipartFile imageFile, Project project) throws IOException
    {
        String imageName = imageFile.getOriginalFilename();
        Path imagePath = Paths.get(pathHelper.getImagePath(project));
        Files.createDirectories(imagePath.getParent());
        Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
        return imageName;
    }
    public String storeBuild(MultipartFile zipFile, Project project) throws IOException
    {
        String buildName = getBuildFolderName(zipFile);
        //Нужна папка проекта, чтобы распаковать в нее zip-файл
        Path projectPath = Paths.get(pathHelper.getProjectPath(project));
        extractZipFromStream(zipFile.getInputStream(), projectPath);
        return buildName;
    }
    public String getBuildFolderName(MultipartFile buildZip)
    {
        String originalName = buildZip.getOriginalFilename();
        if (originalName.toLowerCase().endsWith(".zip"))
        {
            return originalName.substring(0, originalName.length() - 4);
        }
        return originalName;
    }
    private void extractZipFromStream(InputStream zipInputStream, Path extractPath) throws IOException
    {
        try (ZipInputStream zipInpStream = new ZipInputStream(zipInputStream))
        {
            ZipEntry zipEntry = zipInpStream.getNextEntry();
            while (zipEntry != null)
            {
                Path newPath = extractPath.resolve(zipEntry.getName());
                if (zipEntry.isDirectory())
                {
                    Files.createDirectories(newPath);
                }
                else
                {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zipInpStream, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zipEntry = zipInpStream.getNextEntry();
            }
            zipInpStream.closeEntry();
        }
    }
    public Resource loadImageFile(Project project, String imageName)
    {
        return loadFile(pathHelper.getProjectPath(project), imageName);
    }
    public Resource loadBuildFile(Project project, String filePath)
    {
        return loadFile(pathHelper.getProjectPath(project), filePath);
    }
    private Resource loadFile(String projectPath, String filePath)
    {
        try
        {
            Path fullPath = Paths.get(projectPath + filePath);
            Resource resource = new FileSystemResource(fullPath);
            if (resource.exists())
            {
                return resource;
            }
            else
            {
                throw new RuntimeException("Файл не найден: " + filePath);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Невозможно прочитать файл: " + filePath, e);
        }
    }

    public boolean hasIndexPage(Project project)
    {
        try
        {
            Path indexPath = Paths.get(pathHelper.getBuildPath(project) + "/index.html");
            return Files.exists(indexPath) && Files.isRegularFile(indexPath);
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public void deleteImage(Project project) throws IOException
    {
        Path imagePath = Paths.get(pathHelper.getImagePath(project));
        if (Files.exists(imagePath))
        {
            Files.delete(imagePath);
        }
    }
    public void deleteBuild(Project project)
    {
        Path buildPath = Paths.get(pathHelper.getBuildPath(project));
        try
        {
            if (Files.exists(buildPath))
            {
                filesHelper.deleteFolder(buildPath);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Не удалось удалить билд проекта: " + project.getId(), e);
        }
    }
    public void deleteProjectFolder(Project project)
    {
        Path projectPath = Paths.get(pathHelper.getProjectPath(project));
        try
        {
            if (Files.exists(projectPath))
            {
                filesHelper.deleteFolder(projectPath);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Не удалось удалить папку проекта: " + project.getId(), e);
        }
    }
}
