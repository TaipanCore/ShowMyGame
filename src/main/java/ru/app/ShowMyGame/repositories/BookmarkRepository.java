package ru.app.ShowMyGame.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.app.ShowMyGame.entities.Bookmark;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public class BookmarkRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Optional<Bookmark> getBookmarkById(Integer id)
    {
        Bookmark bookmark = entityManager.find(Bookmark.class, id);
        return Optional.ofNullable(bookmark);
    }

    @Transactional
    public Optional<Bookmark> getBookmarkByUserAndProject(User user, Project project)
    {
        try
        {
            TypedQuery<Bookmark> query = entityManager.createQuery("FROM Bookmark b WHERE b.user = :user AND b.project = :project", Bookmark.class);
            query.setParameter("user", user);
            query.setParameter("project", project);
            return Optional.of(query.getSingleResult());
        }
        catch (NoResultException e)
        {
            return Optional.empty();
        }
    }

    @Transactional
    public List<Bookmark> getBookmarksByUser(User user)
    {
        TypedQuery<Bookmark> query = entityManager.createQuery("FROM Bookmark b WHERE b.user = :user ORDER BY b.createdAt DESC", Bookmark.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Transactional
    public List<Bookmark> getBookmarksByProject(Project project)
    {
        TypedQuery<Bookmark> query = entityManager.createQuery("FROM Bookmark b WHERE b.project = :project ORDER BY b.createdAt DESC", Bookmark.class);
        query.setParameter("project", project);
        return query.getResultList();
    }

    @Transactional
    public Bookmark addNewBookmark(Bookmark bookmark)
    {
        entityManager.persist(bookmark);
        return bookmark;
    }

    @Transactional
    public void deleteBookmark(Bookmark bookmark)
    {
        entityManager.remove(bookmark);
    }
}