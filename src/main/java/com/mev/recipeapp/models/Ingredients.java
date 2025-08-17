package com.mev.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ingredients {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    public Ingredients(String description) {
        this.description = description;
    }
}
