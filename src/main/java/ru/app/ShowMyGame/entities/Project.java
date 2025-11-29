package ru.app.ShowMyGame.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Table(name="projects")
public class Project
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer author_id;
    private String title;
    private String description;
    private ArrayList<String> tags;
    private String genre;
    private String buildType;
    private String buildFileName;
    private String imageFileName;
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

    public String getBuildType()
    {
        return buildType;
    }
    public void setBuildType(String buildType)
    {
        this.buildType = buildType;
    }
    public String getBuildFileName() {return buildFileName;}
    public void setBuildFileName(String buildFileName) {this.buildFileName = buildFileName;}

    public String getImageFileName()
    {
        return imageFileName;
    }
    public void setImageFileName(String imageFileName)
    {
        this.imageFileName = imageFileName;
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
