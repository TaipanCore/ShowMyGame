package ru.app.ShowMyGame.helpers;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Component
public class FilesHelper
{
    public String getExtension(MultipartFile file)
    {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains("."))
        {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return fileExtension;
    }
    public void deleteFolder(String folderPath) throws IOException
    {
        Path path = Paths.get(folderPath);
        if (Files.exists(path))
        {
            Files.walk(path).sorted(Comparator.reverseOrder()).forEach(p -> {
                try
                {
                    makeWritable(p);
                    Files.delete(p);
                }
                catch (AccessDeniedException e)
                {
                    System.err.println("Доступ запрещен: " + p + " - " + e.getMessage());
                }
                catch (IOException e)
                {
                    System.err.println("Ошибка удаления: " + p + " - " + e.getMessage());
                }
            });
        }
    }

    private void makeWritable(Path path) {
        try {
            File file = path.toFile();
            file.setReadable(true);
            file.setWritable(true);
            file.setExecutable(true);
            if (System.getProperty("os.name").toLowerCase().contains("win"))
            {
                Files.setAttribute(path, "dos:readonly", false);
            }
        }
        catch (Exception e)
        {
            System.err.println("Не удалось изменить атрибуты: " + path);
        }
    }
}
