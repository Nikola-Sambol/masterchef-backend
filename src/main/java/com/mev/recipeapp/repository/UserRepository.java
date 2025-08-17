package com.mev.recipeapp.repository;

import com.mev.recipeapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.email = :email")
    User findByEmailWithRole(@Param("email") String email);
    Boolean existsByEmail(String email);
}
