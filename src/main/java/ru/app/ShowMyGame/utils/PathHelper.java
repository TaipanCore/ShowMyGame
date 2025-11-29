package ru.app.ShowMyGame.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.app.ShowMyGame.entities.Project;

@Component
public class PathHelper
{
    @Value("${upload.base-folder}")
    private String baseUploadDir;

    public String getProjectPath(Project project)
    {
        return baseUploadDir + "/" + project.getId() + "/";
    }
    public String getImagePath(Project project)
    {
        return getProjectPath(project) + project.getImageFileName();
    }
    public String getBuildPath(Project project)
    {
        return getProjectPath(project) + project.getBuildFileName();
    }
}
