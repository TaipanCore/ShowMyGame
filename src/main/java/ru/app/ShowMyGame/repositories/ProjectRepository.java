package ru.app.ShowMyGame.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.app.ShowMyGame.entities.Project;

import java.util.List;

@Repository
public class ProjectRepository
{
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Project getProjectById(Integer id)
    {
        return em.find(Project.class, id);
    }
    @Transactional
    public List<Project> getAllProjects()
    {
        TypedQuery<Project> query = em.createQuery("FROM Project", Project.class);
        return query.getResultList();
    }
    @Transactional
    public Project addNewProject(Project project)
    {
        em.persist(project);
        return project;
    }
    @Transactional
    public Project editProject(Project project)
    {
        em.merge(project);
        return project;
    }
    @Transactional
    public void deleteProjectById(Project project)
    {
        em.remove(project);
    }
}
