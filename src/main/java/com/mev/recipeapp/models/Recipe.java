package com.mev.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="recipe_name", columnDefinition="TEXT")
    private String recipeName;

    @Column(name="creation_date", updatable = false, insertable = false)
    private LocalDate creationDate;

    @Lob
    @Column(name="image", columnDefinition="LONGBLOB")
    private byte[] image;

    @Lob
    @Column(name="video", columnDefinition="LONGBLOB")
    private byte[] video;

    @Column(name="preparation_time", columnDefinition="TEXT")
    private String preparationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Component> components;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    @JsonIgnore
    private Category category;

    public Recipe(String recipeName, byte[] image, byte[] video, String preparationTime, User user, Category category) {
        this.recipeName = recipeName;
        this.image = image;
        this.video = video;
        this.preparationTime = preparationTime;
        this.user = user;
        this.category = category;
    }
}
