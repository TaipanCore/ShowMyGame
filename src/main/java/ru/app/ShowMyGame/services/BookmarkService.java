package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.app.ShowMyGame.entities.Bookmark;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.repositories.BookmarkRepository;
import ru.app.ShowMyGame.repositories.ProjectRepository;
import ru.app.ShowMyGame.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService
{
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public Bookmark addToBookmarks(Integer projectId, User user)
    {
        Project project = projectRepository.getProjectById(projectId).orElseThrow(() -> new RuntimeException("Проект не найден"));
        Optional<Bookmark> existingBookmark = bookmarkRepository.getBookmarkByUserAndProject(user, project);
        if (existingBookmark.isPresent())
        {
            throw new RuntimeException("Проект уже в избранном");
        }
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setProject(project);
        return bookmarkRepository.addNewBookmark(bookmark);
    }

    public void deleteFromBookmarks(Integer projectId, User user)
    {
        Project project = projectRepository.getProjectById(projectId).orElseThrow(() -> new RuntimeException("Проект не найден"));
        Optional<Bookmark> bookmark = bookmarkRepository.getBookmarkByUserAndProject(user, project);
        if (bookmark.isEmpty())
        {
            throw new RuntimeException("Проект не найден в избранном");
        }
        bookmarkRepository.deleteBookmark(bookmark.get());
    }

    public boolean isBookmarked(Integer projectId, User user)
    {
        Project project = projectRepository.getProjectById(projectId).orElseThrow(() -> new RuntimeException("Проект не найден"));
        return bookmarkRepository.getBookmarkByUserAndProject(user, project).isPresent();
    }

    public List<Bookmark> getUserBookmarks(User user)
    {
        return bookmarkRepository.getBookmarksByUser(user);
    }
}