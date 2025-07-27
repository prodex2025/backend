package com.backend.backend.domain.repository;

import com.backend.backend.domain.model.Allergy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AllergyRepository  extends JpaRepository<Allergy, UUID> {
}
