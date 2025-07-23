package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.ItemSaveRequest;
import com.example.oil_mart.dto.request.ItemUpdateRequest;
import com.example.oil_mart.dto.response.ItemResponse;
import com.example.oil_mart.repository.ItemRepository;
import com.example.oil_mart.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImplementation implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public ItemResponse save(ItemSaveRequest saveRequest) {
        return null;
    }

    @Override
    public List<ItemResponse> getAll() {
        return null;
    }

    @Override
    public ItemResponse getById(Integer id) {
        return null;
    }

    @Override
    public ItemResponse update(ItemUpdateRequest updateRequest) {
        return null;
    }
}
