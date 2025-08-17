package com.mev.recipeapp.repository;

import com.mev.recipeapp.models.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructionRepository extends JpaRepository<Instruction, Long> {

}
