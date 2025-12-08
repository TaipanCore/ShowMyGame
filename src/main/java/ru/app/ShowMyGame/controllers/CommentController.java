package ru.app.ShowMyGame.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.app.ShowMyGame.entities.Comment;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.services.CommentService;
import ru.app.ShowMyGame.services.ProjectService;
import ru.app.ShowMyGame.services.UserService;
import ru.app.ShowMyGame.helpers.SessionHelper;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController
{
    @Autowired
    private CommentService commentService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionHelper sessionHelper;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getProjectComments(@PathVariable Integer projectId)
    {
        try
        {
            Project project = projectService.getProjectById(projectId);
            List<Comment> comments = commentService.getCommentsByProject(project);
            List<Map<String, Object>> commentDtos = comments.stream().map(comment ->
                    {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", comment.getId());
                        dto.put("text", comment.getText());
                        dto.put("createdAt", comment.getCreatedAt());

                        Map<String, Object> userDto = new HashMap<>();
                        userDto.put("id", comment.getUser().getId());
                        userDto.put("username", comment.getUser().getUsername());
                        userDto.put("avatarFileName", comment.getUser().getAvatarFileName());
                        dto.put("user", userDto);

                        return dto;
                    }).toList();

            return ResponseEntity.ok(Map.of("success", true, "comments", commentDtos, "count", comments.size()));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> createComment(@PathVariable Integer projectId, @RequestParam String text, HttpServletRequest request)
    {
        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }
            Project project = projectService.getProjectById(projectId);
            Comment comment = commentService.createComment(project, currentUser, text);

            return ResponseEntity.ok(Map.of("success", true, "message", "Комментарий добавлен", "comment", Map.of("id", comment.getId(), "text", comment.getText(), "createdAt", comment.getCreatedAt(), "user", Map.of("id", currentUser.getId(), "username", currentUser.getUsername(), "avatarFileName", currentUser.getAvatarFileName()))));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Integer commentId, @RequestParam String text, HttpServletRequest request)
    {

        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }
            Comment updatedComment = commentService.updateComment(commentId, text);
            return ResponseEntity.ok(Map.of("success", true, "message", "Комментарий обновлен", "comment", Map.of("id", updatedComment.getId(), "text", updatedComment.getText())));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId, HttpServletRequest request)
    {
        try
        {
            User currentUser = sessionHelper.getCurrentUser();
            if (currentUser == null)
            {
                return ResponseEntity.status(401).body(Map.of("success", false, "error", "Требуется авторизация"));
            }
            commentService.deleteComment(commentId, currentUser);
            return ResponseEntity.ok(Map.of("success", true, "message", "Комментарий удален"));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserComments(@PathVariable Integer userId)
    {
        try
        {
            User user = userService.getUserById(userId);
            List<Comment> comments = commentService.getCommentsByUser(user);
            return ResponseEntity.ok(Map.of("success", true, "comments", comments, "count", comments.size()));
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}