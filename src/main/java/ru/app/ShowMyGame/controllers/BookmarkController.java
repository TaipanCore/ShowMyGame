package ru.app.ShowMyGame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.app.ShowMyGame.entities.Bookmark;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.BookmarkService;
import ru.app.ShowMyGame.services.ProjectService;
import ru.app.ShowMyGame.helpers.SessionHelper;

import java.util.List;
import java.util.Map;

@Controller
public class BookmarkController
{
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private SessionHelper sessionHelper;

    @GetMapping("/bookmarks")
    public String myBookmarksPage(Model model)
    {
        User currentUser = sessionHelper.getCurrentUser();
        if (currentUser == null)
        {
            return "redirect:/auth";
        }
        List<Bookmark> bookmarks = bookmarkService.getUserBookmarks(currentUser);
        List<Project> projects = bookmarks.stream().map(Bookmark::getProject).toList();
        model.addAttribute("bookmarks", bookmarks);
        model.addAttribute("projects", projects);
        model.addAttribute("bookmarkCount", projects.size());
        return "bookmarks-page";
    }

    @PostMapping("/bookmarks/api/project/{id}/add")
    @ResponseBody
    public ResponseEntity<?> addBookmark(@PathVariable Integer id)
    {
        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }
            bookmarkService.addToBookmarks(id, currentUser);

            return ResponseEntity.ok(Map.of("success", true, "message", "Проект добавлен в избранное"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/bookmarks/api/project/{id}/delete")
    @ResponseBody
    public ResponseEntity<?> deleteBookmark(@PathVariable Integer id)
    {
        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }
            bookmarkService.deleteFromBookmarks(id, currentUser);
            return ResponseEntity.ok(Map.of("success", true, "message", "Проект удален из избранного"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}