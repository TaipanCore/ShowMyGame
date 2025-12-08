package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.helpers.FilesHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileService
{
    @Value("${upload.base-folder}")
    public String baseUploadDir;
    @Autowired
    private FilesHelper filesHelper;

    public String storeUserAvatar(MultipartFile avatarFile, User user) throws IOException
    {
        String fileName = "userAvatar_" + user.getId() + filesHelper.getExtension(avatarFile);
        Path filePath = Paths.get(baseUploadDir + "/avatars/users/" + fileName);
        Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
    public Resource loadUserAvatar(User user)
    {
        return loadFile(baseUploadDir + "/avatars/users/" + user.getAvatarFileName());
    }
    public void deleteUserAvatar(User user)
    {
        deleteFile(baseUploadDir + "/avatars/users/" + user.getAvatarFileName());
    }

    public String storeImage(MultipartFile imageFile, Project project) throws IOException
    {
        String fileName = "projectImage_" + project.getId() + filesHelper.getExtension(imageFile);
        Path filePath = Paths.get(baseUploadDir + "/avatars/projects/" + fileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
    public Resource loadImage(Project project)
    {
        return loadFile(baseUploadDir + "/avatars/projects/" + project.getImageFileName());
    }
    public void deleteImage(Project project)
    {
        deleteFile(baseUploadDir + "/avatars/projects/" + project.getImageFileName());
    }

    public String storeBuild(MultipartFile zipFile, Project project) throws IOException
    {
        String folderName = "buildFolder_" + project.getId();
        Path folderPath = Paths.get(baseUploadDir + "/builds/" + folderName);
        extractZipFromStream(zipFile, folderPath);
        return folderName;
    }
    public Resource loadBuild(Project project)
    {
        return loadFile(baseUploadDir + "/builds/" + project.getBuildFolderName());
    }
    public void deleteBuild(Project project) throws IOException
    {
        filesHelper.deleteFolder(baseUploadDir + "/builds/" + project.getBuildFolderName());
    }

    public void deleteProjectFiles(Project project) throws IOException
    {
        deleteBuild(project);
        deleteImage(project);
    }

    private void extractZipFromStream(MultipartFile zipFile, Path extractPath) throws IOException
    {
        try (ZipInputStream zipInpStream = new ZipInputStream(zipFile.getInputStream()))
        {
            ZipEntry zipEntry = zipInpStream.getNextEntry();
            while (zipEntry != null)
            {
                String entryName = zipEntry.getName();
                int slashIndex = entryName.indexOf("/");
                if (slashIndex != -1)
                {
                    entryName = entryName.substring(slashIndex + 1);
                }
                if (!entryName.isEmpty())
                {
                    Path newPath = extractPath.resolve(entryName);
                    if (zipEntry.isDirectory())
                    {
                        Files.createDirectories(newPath);
                    }
                    else
                    {
                        Files.createDirectories(newPath.getParent());
                        Files.copy(zipInpStream, newPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
                zipEntry = zipInpStream.getNextEntry();
            }
            zipInpStream.closeEntry();
        }
    }
    private Resource loadFile(String filePath)
    {
        try
        {
            Resource resource = new FileSystemResource(Paths.get(filePath));
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
    private void deleteFile(String filePath)
    {
        try
        {
            Path path = Paths.get(filePath);
            if (Files.exists(path))
            {
                Files.delete(path);
            }
            else
            {
                throw new RuntimeException("Файл не найден: " + filePath);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Невозможно удалить файл: " + filePath, e);
        }
    }
    public boolean hasIndexPage(Project project)
    {
        try
        {
            File buildFolder = loadBuild(project).getFile();
            Path indexPath = Paths.get(buildFolder.getPath(), "index.html");
            return Files.exists(indexPath) && Files.isRegularFile(indexPath);
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
