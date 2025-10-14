package com.example.oil_mart.repository;

import com.example.oil_mart.model.SupplierItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierItemRepository extends JpaRepository<SupplierItem, Long> {
    List<SupplierItem> findBySupplier_Id(Long supplierId);
    List<SupplierItem> findByItem_Id(Long itemId);
    Optional<SupplierItem> findBySupplier_IdAndItem_Id(Long supplierId, Integer itemId);
}
