package com.mev.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name", columnDefinition="TEXT")
    private String name;

    @OneToMany(mappedBy="category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Recipe> recipes;

    public Category(String name) {
        this.name = name;
    }
}
