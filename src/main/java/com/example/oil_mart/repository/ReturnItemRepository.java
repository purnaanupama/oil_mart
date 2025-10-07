package com.example.oil_mart.repository;

import com.example.oil_mart.model.Return;
import com.example.oil_mart.model.Return_item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReturnItemRepository extends JpaRepository<Return_item,Long> {
    List<Return_item> findByReturnRecord(Return returnRecord);
}
