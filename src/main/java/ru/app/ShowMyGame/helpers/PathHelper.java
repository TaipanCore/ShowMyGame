package ru.app.ShowMyGame.helpers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathHelper
{
    @Value("${upload.base-folder}")
    public String baseUploadDir;
}
