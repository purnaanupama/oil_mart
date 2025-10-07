package com.example.oil_mart.repository;

import com.example.oil_mart.model.SupplierItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierItemRepository extends JpaRepository<SupplierItem, Long> {
    List<SupplierItem> findBySupplier_Id(Long supplierId);
    List<SupplierItem> findByItem_Id(Long itemId);
}
