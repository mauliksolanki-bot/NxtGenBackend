package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.FormFieldEntity;

public interface FormFieldRepository extends JpaRepository<FormFieldEntity, Long> {

    List<FormFieldEntity> findByFormIdOrderByDisplayOrderAsc(Long formId);
}
