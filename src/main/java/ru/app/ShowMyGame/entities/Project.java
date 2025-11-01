package ru.app.ShowMyGame.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

@Entity
@Table(name="projects")
public class Project
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer author_id;
    private String title;
    private String description;
    private ArrayList<String> tags;
    private String genre;
    private String buildPath;
    private String imagePath;
    private LocalDate created_at;

    public Integer getId()
    {
        return id;
    }
    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getAuthor_id()
    {
        return author_id;
    }
    public void setAuthor_id(Integer author_id)
    {
        this.author_id = author_id;
    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public ArrayList<String> getTags()
    {
        return tags;
    }
    public void setTags(ArrayList<String> tags)
    {
        this.tags = tags;
    }

    public String getGenre()
    {
        return genre;
    }
    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public String getBuildPath() {return buildPath;}
    public void setBuildPath(String buildPath) {this.buildPath = buildPath;}

    public String getImagePath()
    {
        return imagePath;
    }
    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
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
