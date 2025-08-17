package com.mev.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="instructions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Instruction {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    public Instruction(String description) {
        this.description = description;
    }
}
