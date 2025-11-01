package ru.app.ShowMyGame.entities;

import java.awt.image.BufferedImage;
import java.time.LocalDate;

public class User
{
    public enum UserRole
    {
        developer,
        publisher,
        studio,
        player
    }

    private Integer id;
    private String username;
    private String email;
    private UserRole role;
    private String description;
    private BufferedImage avatar;
    private LocalDate created_at;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public UserRole getRole()
    {
        return role;
    }

    public void setRole(UserRole role)
    {
        this.role = role;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public BufferedImage getAvatar()
    {
        return avatar;
    }

    public void setAvatar(BufferedImage avatar)
    {
        this.avatar = avatar;
    }

    public LocalDate getCreated_at()
    {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at)
    {
        this.created_at = created_at;
    }
}
