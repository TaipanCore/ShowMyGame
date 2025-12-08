package ru.app.ShowMyGame.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
@Table(name="projects")
public class Project
{
    public Project()
    {
        this.createdAt = LocalDate.now();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
    private String title;
    private String description;
    private String tags;
    private String genres;
    private String buildType;
    private String buildFolderName;
    private String imageFileName;
    private LocalDate createdAt;

    public Integer getId()
    {
        return id;
    }
    public void setId(Integer id)
    {
        this.id = id;
    }

    public User getAuthor()
    {
        return author;
    }
    public void setAuthor(User author)
    {
        this.author = author;
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
        if (tags == null || tags.trim().isEmpty())
        {
            return new ArrayList<>();
        }
        return (ArrayList<String>) Arrays.asList(tags.split(","));
    }
    public void setTags(ArrayList<String> tags)
    {
        if (tags == null || tags.isEmpty())
        {
            this.tags = null;
        }
        else
        {
            this.tags = String.join(",", tags);
        }
    }

    public ArrayList<String> getGenres()
    {
        if (genres == null || genres.trim().isEmpty())
        {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(genres.split(",")));
    }
    public String getGenresAsString()
    {
        if (genres == null || genres.trim().isEmpty())
        {
            return "";
        }
        return genres.replace(",", ", ");
    }
    public void setGenres(ArrayList<String> genres)
    {
        if (genres == null || genres.isEmpty())
        {
            this.genres = null;
        }
        else
        {
            this.genres = String.join(",", genres);
        }
    }

    public String getBuildType()
    {
        return buildType;
    }
    public void setBuildType(String buildType)
    {
        this.buildType = buildType;
    }
    public String getBuildFolderName() {return buildFolderName;}
    public void setBuildFolderName(String buildFolderName) {this.buildFolderName = buildFolderName;}

    public String getImageFileName()
    {
        return imageFileName;
    }
    public void setImageFileName(String imageFileName)
    {
        this.imageFileName = imageFileName;
    }

    public LocalDate getCreatedAt()
    {
        return createdAt;
    }
    public void setCreatedAt(LocalDate createdAt)
    {
        this.createdAt = createdAt;
    }
}
