package com.mev.recipeapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", columnDefinition="TEXT")
    private String name;

    @Column(name="surname", columnDefinition="TEXT")
    private String surname;

    @Column(name="email", columnDefinition="TEXT", unique = true)
    private String email;

    @Column(name="password", columnDefinition="TEXT")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    @JsonIgnore
    private Role role;

    @OneToMany(mappedBy="user", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Recipe> recipes;

    private boolean enabled;

    @CreationTimestamp
    @Column(updatable = false, name = "creation_date")
    private LocalDateTime createdDate;

    public User(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.enabled = true;
    }
}
