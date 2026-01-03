package com.gestion.repository;

import com.gestion.entity.DevisDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevisDetailRepository extends JpaRepository<DevisDetail, Long> {
    List<DevisDetail> findByDevisId(Long devisId);

    void deleteByDevisId(Long devisId);
}
