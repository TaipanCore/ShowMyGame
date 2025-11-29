package ru.app.ShowMyGame.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Component
public class FilesHelper
{
    public void deleteFolder(Path projectPath) throws IOException
    {
        Files.walk(projectPath).sorted(Comparator.reverseOrder()).forEach(p -> {
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
