package ru.app.ShowMyGame.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.app.ShowMyGame.entities.Comment;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository
{
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public Optional<Comment> getCommentById(Integer id)
    {
        Comment comment = entityManager.find(Comment.class, id);
        return Optional.ofNullable(comment);
    }

    @Transactional
    public List<Comment> getCommentsByProject(Project project)
    {
        TypedQuery<Comment> query = entityManager.createQuery("FROM Comment c WHERE c.project = :project ORDER BY c.createdAt DESC", Comment.class);
        query.setParameter("project", project);
        return query.getResultList();
    }

    @Transactional
    public List<Comment> getCommentsByUser(User user)
    {
        TypedQuery<Comment> query = entityManager.createQuery("FROM Comment c WHERE c.user = :user ORDER BY c.createdAt DESC", Comment.class
        );
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Transactional
    public int countCommentsByProject(Project project)
    {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(c) FROM Comment c WHERE c.project = :project", Long.class);
        query.setParameter("project", project);
        return query.getSingleResult().intValue();
    }

    @Transactional
    public Comment addComment(Comment comment)
    {
        entityManager.persist(comment);
        return comment;
    }

    @Transactional
    public Comment updateComment(Comment comment)
    {
        return entityManager.merge(comment);
    }

    @Transactional
    public void deleteComment(Comment comment)
    {
        entityManager.remove(comment);
    }

    @Transactional
    public void deleteCommentsByProject(Project project)
    {
        TypedQuery<Comment> query = entityManager.createQuery("FROM Comment c WHERE c.project = :project", Comment.class);
        query.setParameter("project", project);
        List<Comment> comments = query.getResultList();
        for (Comment comment : comments)
        {
            entityManager.remove(comment);
        }
    }

    @Transactional
    public void deleteCommentsByUser(User user)
    {
        TypedQuery<Comment> query = entityManager.createQuery("FROM Comment c WHERE c.user = :user", Comment.class);
        query.setParameter("user", user);
        List<Comment> comments = query.getResultList();
        for (Comment comment : comments)
        {
            entityManager.remove(comment);
        }
    }

    @Transactional
    public List<Comment> getAllComments()
    {
        TypedQuery<Comment> query = entityManager.createQuery("FROM Comment", Comment.class);
        return query.getResultList();
    }
}