package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.ItemSaveRequest;
import com.example.oil_mart.dto.request.ItemUpdateRequest;
import com.example.oil_mart.dto.response.ItemResponse;
import com.example.oil_mart.dto.response.PageResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {

    ItemResponse save(ItemSaveRequest saveRequest);

    List<ItemResponse> getAll();

    PageResponse<ItemResponse> getAll(int page, int size, String sortBy, String sortDir);

    ItemResponse getById(Integer id);

    ItemResponse update(ItemUpdateRequest updateRequest);

    ItemResponse deleteById(Integer Id);

    List<ItemResponse> getAllByNotEmptyQuatity();

}
