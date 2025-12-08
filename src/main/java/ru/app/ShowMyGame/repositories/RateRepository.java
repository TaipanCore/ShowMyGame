package ru.app.ShowMyGame.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.app.ShowMyGame.entities.Rate;
import ru.app.ShowMyGame.entities.Project;
import ru.app.ShowMyGame.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public class RateRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Optional<Rate> getRateById(Integer id)
    {
        Rate rate = entityManager.find(Rate.class, id);
        return Optional.ofNullable(rate);
    }

    @Transactional
    public Optional<Rate> getRateByProjectAndUser(Project project, User user)
    {
        try
        {
            TypedQuery<Rate> query = entityManager.createQuery("FROM Rate r WHERE r.project = :project AND r.user = :user", Rate.class
            );
            query.setParameter("project", project);
            query.setParameter("user", user);
            return Optional.of(query.getSingleResult());
        }
        catch (NoResultException e)
        {
            return Optional.empty();
        }
    }

    @Transactional
    public List<Rate> getRatesByProject(Project project)
    {
        TypedQuery<Rate> query = entityManager.createQuery("FROM Rate r WHERE r.project = :project ORDER BY r.createdAt DESC", Rate.class);
        query.setParameter("project", project);
        return query.getResultList();
    }

    @Transactional
    public List<Rate> getRatesByUser(User user)
    {
        TypedQuery<Rate> query = entityManager.createQuery("FROM Rate r WHERE r.user = :user ORDER BY r.createdAt DESC", Rate.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Transactional
    public int countRatesByProject(Project project)
    {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(r) FROM Rate r WHERE r.project = :project", Long.class
        );
        query.setParameter("project", project);
        return query.getSingleResult().intValue();
    }

    @Transactional
    public double calculateAverageRateForProject(Project project)
    {
        TypedQuery<Double> query = entityManager.createQuery("SELECT AVG(r.rate) FROM Rate r WHERE r.project = :project", Double.class
        );
        query.setParameter("project", project);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }

    @Transactional
    public Rate addRate(Rate rate)
    {
        entityManager.persist(rate);
        return rate;
    }

    @Transactional
    public Rate updateRate(Rate rate)
    {
        return entityManager.merge(rate);
    }

    @Transactional
    public void deleteRate(Rate rate)
    {
        entityManager.remove(rate);
    }

    @Transactional
    public void deleteRatesByProject(Project project)
    {
        TypedQuery<Rate> query = entityManager.createQuery("FROM Rate r WHERE r.project = :project", Rate.class);
        query.setParameter("project", project);
        List<Rate> rates = query.getResultList();

        for (Rate rate : rates)
        {
            entityManager.remove(rate);
        }
    }

    @Transactional
    public void deleteRatesByUser(User user)
    {
        TypedQuery<Rate> query = entityManager.createQuery("FROM Rate r WHERE r.user = :user", Rate.class);
        query.setParameter("user", user);
        List<Rate> rates = query.getResultList();
        for (Rate rate : rates)
        {
            entityManager.remove(rate);
        }
    }

    @Transactional
    public List<Rate> getAllRates()
    {
        TypedQuery<Rate> query = entityManager.createQuery("FROM Rate", Rate.class);
        return query.getResultList();
    }
}