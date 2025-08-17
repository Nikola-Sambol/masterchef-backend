package com.mev.recipeapp.repository;

import com.mev.recipeapp.models.AppRole;
import com.mev.recipeapp.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
