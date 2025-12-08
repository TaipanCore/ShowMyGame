package ru.app.ShowMyGame.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.FileService;
import ru.app.ShowMyGame.services.ProjectService;
import ru.app.ShowMyGame.services.UserService;

import java.io.File;

@RestController
@RequestMapping("/files")
public class FileController
{
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @GetMapping("/projects/{id}/images/{filename:.+}")
    public ResponseEntity<Resource> getProjectImage(@PathVariable Integer id, @PathVariable String filename)
    {
        try
        {
            Project project = projectService.getProjectById(id);
            Resource imageResource = fileService.loadImage(project);
            String contentType = determineContentType(filename);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(imageResource);
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/projects/{id}/build/**")
    public ResponseEntity<Resource> getBuild(@PathVariable Integer id, HttpServletRequest request)
    {
        try
        {
            Project project = projectService.getProjectById(id);
            String requestPath = request.getRequestURI();
            String relativePath = extractBuildFilePath(requestPath, id);
            File buildFolder = fileService.loadBuild(project).getFile();
            if (relativePath.isEmpty() || relativePath.equals("/"))
            {
                relativePath = "index.html";
            }
            File file = new File(buildFolder, relativePath);
            if (!file.exists() || file.isDirectory())
            {
                file = new File(buildFolder, "index.html");
            }
            Resource fileResource = new FileSystemResource(file);
            String contentType = determineContentType(file.getName());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(fileResource);
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{id}/avatar")
    public ResponseEntity<Resource> getUserAvatar(@PathVariable Integer id)
    {
        try
        {
            User user = userService.getUserById(id);
            Resource avatar = fileService.loadUserAvatar(user);
            String contentType = determineContentType(user.getAvatarFileName());

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(avatar);
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    private String extractBuildFilePath(String requestUri, Integer id)
    {
        String prefix = "/files/projects/" + id + "/build/";
        if (requestUri.contains(prefix))
        {
            String path = requestUri.substring(requestUri.indexOf(prefix) + prefix.length());
            return path.startsWith("/") ? path.substring(1) : path;
        }
        return "";
    }

    private String determineContentType(String filename)
    {
        if (filename == null || !filename.contains(".")) {
            return "application/octet-stream";
        }

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        switch (extension)
        {
            case "html": return "text/html";
            case "js": return "application/javascript";
            case "css": return "text/css";
            case "png": return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "gif": return "image/gif";
            case "json": return "application/json";
            case "wasm": return "application/wasm";
            case "ico": return "image/x-icon";
            case "txt": return "text/plain";
            default: return "application/octet-stream";
        }
    }
}