package ru.app.ShowMyGame.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.services.FileService;
import ru.app.ShowMyGame.services.ProjectService;

@RestController
@RequestMapping("/files")
public class FileController
{
    @Autowired
    private ProjectService projectService;
    @Autowired
    private FileService fileService;

    @GetMapping("/projects/{id}/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable Integer id, @PathVariable String filename)
    {
        try
        {
            Project project = projectService.getProjectById(id);
            Resource file = fileService.loadImageFile(project, filename);
            String contentType = determineContentType(filename);
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(file);
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
            String filePath = project.getBuildFileName() + "/" + extractFilePath(requestPath, id);
            Resource file = fileService.loadBuildFile(project, filePath);
            String contentType = determineContentType(filePath);

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, contentType).body(file);
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    private String extractFilePath(String requestUri, Integer id)
    {
        String prefix = "/files/projects/" + id + "/build/";
        return requestUri.substring(requestUri.indexOf(prefix) + prefix.length());
    }

    private String determineContentType(String filename)
    {
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
            default: return "application/octet-stream";
        }
    }
}
