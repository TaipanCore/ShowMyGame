package ru.app.ShowMyGame.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.app.ShowMyGame.entities.Comment;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;
import ru.app.ShowMyGame.repositories.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Project project, User user, String text)
    {
        if (text == null || text.trim().isEmpty())
        {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }

        Comment comment = new Comment(project, user, text.trim());
        return commentRepository.addComment(comment);
    }

    public Comment updateComment(Integer commentId, String newText)
    {
        if (newText == null || newText.trim().isEmpty())
        {
            throw new IllegalArgumentException("Текст комментария не может быть пустым");
        }

        Comment comment = commentRepository.getCommentById(commentId).orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        comment.setText(newText.trim());
        return commentRepository.updateComment(comment);
    }

    public void deleteComment(Integer commentId, User currentUser)
    {
        Comment comment = commentRepository.getCommentById(commentId).orElseThrow(() -> new RuntimeException("Комментарий не найден"));
        commentRepository.deleteComment(comment);
    }

    public List<Comment> getCommentsByProject(Project project)
    {
        return commentRepository.getCommentsByProject(project);
    }

    public List<Comment> getCommentsByUser(User user)
    {
        return commentRepository.getCommentsByUser(user);
    }

    public int getCommentCountForProject(Project project)
    {
        return commentRepository.countCommentsByProject(project);
    }

    public Comment getCommentById(Integer commentId)
    {
        return commentRepository.getCommentById(commentId).orElseThrow(() -> new RuntimeException("Комментарий не найден"));
    }
}