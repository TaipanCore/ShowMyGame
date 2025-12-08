package ru.app.ShowMyGame.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import ru.app.ShowMyGame.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Optional<User> getUserById(Integer id)
    {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }
    @Transactional
    public List<User> getAllUsers()
    {
        TypedQuery<User> query = entityManager.createQuery("FROM User", User.class);
        return query.getResultList();
    }
    @Transactional
    public User addNewUser(User user)
    {
        entityManager.persist(user);
        return user;
    }
    @Transactional
    public User editUser(User user)
    {
        entityManager.merge(user);
        return user;
    }
    @Transactional
    public void deleteUser(User user)
    {
        entityManager.remove(user);
    }
    @Transactional
    public Optional<User> getUserByEmail(String email)
    {
        try
        {
            TypedQuery<User> query = entityManager.createQuery("FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        }
        catch (NoResultException e)
        {
            return Optional.empty();
        }
    }
}
