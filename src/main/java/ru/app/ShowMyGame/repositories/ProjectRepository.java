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
    private EntityManager entityManager;

    @Transactional
    public Project getProjectById(Integer id)
    {
        return entityManager.find(Project.class, id);
    }
    @Transactional
    public List<Project> getAllProjects()
    {
        TypedQuery<Project> query = entityManager.createQuery("FROM Project", Project.class);
        return query.getResultList();
    }
    @Transactional
    public Project addNewProject(Project project)
    {
        entityManager.persist(project);
        return project;
    }
    @Transactional
    public Project editProject(Project project)
    {
        entityManager.merge(project);
        return project;
    }
    @Transactional
    public void deleteProject(Project project)
    {
        entityManager.remove(project);
    }
}
