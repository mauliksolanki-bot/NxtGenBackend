package com.nxtgen.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.PopupConfigEntity;

public interface PopupConfigRepository extends JpaRepository<PopupConfigEntity, Long> {

    Optional<PopupConfigEntity> findByPopupName(String popupName);
}
