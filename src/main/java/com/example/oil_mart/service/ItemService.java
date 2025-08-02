package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.ItemSaveRequest;
import com.example.oil_mart.dto.request.ItemUpdateRequest;
import com.example.oil_mart.dto.response.ItemResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {

    ItemResponse save(ItemSaveRequest saveRequest);

    List<ItemResponse> getAll();

    ItemResponse getById(Integer id);

    ItemResponse update(ItemUpdateRequest updateRequest);

    ItemResponse deleteById(Integer Id);

    List<ItemResponse> getAllByNotEmptyQuatity();

}
