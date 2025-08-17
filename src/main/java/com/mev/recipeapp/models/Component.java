package com.mev.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Component {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="component_name", columnDefinition="TEXT")
    private String componentName;

    @Column(name="image_path", columnDefinition="TEXT")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name="recipe_id")
    @JsonIgnore
    private Recipe recipe;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="ingredients_id", unique = true)
    @JsonIgnore
    private Ingredients ingredients;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="instruction_id", unique = true)
    @JsonIgnore
    private Instruction instruction;

    public Component(String componentName, String imagePath, Recipe recipe, Ingredients ingredients, Instruction instruction) {
        this.componentName = componentName;
        this.imagePath = imagePath;
        this.recipe = recipe;
        this.ingredients = ingredients;
        this.instruction = instruction;
    }
}
