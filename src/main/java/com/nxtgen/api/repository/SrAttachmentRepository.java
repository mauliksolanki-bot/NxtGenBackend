package com.nxtgen.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SrAttachmentEntity;

public interface SrAttachmentRepository extends JpaRepository<SrAttachmentEntity, Long> {

    // Projection keeps the file bytes out of list queries.
    interface AttachmentView {
        Long getId();

        String getFileName();

        String getContentType();

        Long getFileSize();
    }

    List<AttachmentView> findBySrIdOrderByIdAsc(String srId);

    Optional<SrAttachmentEntity> findByIdAndSrId(Long id, String srId);

    long countBySrId(String srId);

    void deleteByIdInAndSrId(List<Long> ids, String srId);

    void deleteBySrId(String srId);
}
