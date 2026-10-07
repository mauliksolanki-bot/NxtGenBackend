package com.nxtgen.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.FormEntity;

public interface FormRepository extends JpaRepository<FormEntity, Long> {

    Optional<FormEntity> findByFormName(String formName);
}
