package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.dto.request.ItemSaveRequest;
import com.example.oil_mart.dto.request.ItemUpdateRequest;
import com.example.oil_mart.dto.response.BrandResponse;
import com.example.oil_mart.dto.response.CategoryResponse;
import com.example.oil_mart.dto.response.ItemResponse;
import com.example.oil_mart.enums.Status;
import com.example.oil_mart.model.Brand;
import com.example.oil_mart.model.Category;
import com.example.oil_mart.model.Item;
import com.example.oil_mart.repository.BrandRepository;
import com.example.oil_mart.repository.CategoryRepository;
import com.example.oil_mart.repository.ItemRepository;
import com.example.oil_mart.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImplementation implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ItemResponse save(ItemSaveRequest saveRequest) {
        try {
            Item item = new Item();

            item.setItemCode(saveRequest.getItemCode());
            item.setItemDescription(saveRequest.getItemDescription());
//            item.setPackSize(saveRequest.getPackSize());
//            item.setPackUnit(saveRequest.getPackUnit());
            item.setWholesalePrice(saveRequest.getWholesalePrice());
            item.setRetailPrice(saveRequest.getRetailPrice());
            item.setItemBrand(brandRepository.getReferenceById(saveRequest.getItemBrand()));
            item.setCreatedBy(saveRequest.getCreatedBy());
            item.setModifiedBy(saveRequest.getCreatedBy());
            item.setStatus(Status.ACTIVE);

            Item saveResponse = itemRepository.save(item);

            return returnResponse(saveResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ItemResponse> getAll() {
        try {
            return itemRepository.findAll().stream().map(ItemServiceImplementation::returnResponse).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemResponse getById(Integer id) {
        try {
            return itemRepository.findById(id).map(ItemServiceImplementation::returnResponse).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemResponse update(ItemUpdateRequest updateRequest) {
        try {
            Item updateResponse = new Item();

            Item item = itemRepository.getReferenceById(updateRequest.getId());

            if (item != null) {
                item.setItemCode(updateRequest.getItemCode());
                item.setItemDescription(updateRequest.getItemDescription());
//                item.setPackSize(updateRequest.getPackSize());
//                item.setPackUnit(updateRequest.getPackUnit());
                item.setWholesalePrice(updateRequest.getWholesalePrice());
                item.setRetailPrice(updateRequest.getRetailPrice());
                item.setItemBrand(brandRepository.getReferenceById(updateRequest.getItemBrand()));
                item.setStatus(updateRequest.getStatus());
                item.setModifiedBy(updateRequest.getModifiedBy());

                updateResponse = itemRepository.save(item);
            }

            return returnResponse(updateResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemResponse deleteById(Integer id) {
        try {
            Item item = itemRepository.findById(id).orElse(null);

            if (item == null) {
                throw new RuntimeException("Item not found with ID: " + id);
            }

            itemRepository.delete(item);
            return returnResponse(item);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting item", e);
        }
    }

    private static ItemResponse returnResponse(Item item) {
        ItemResponse response = new ItemResponse();

        response.setId(item.getId());
        response.setItemCode(item.getItemCode());
        response.setItemDescription(item.getItemDescription());
//        response.setPackSize(item.getPackSize());
//        response.setPackUnit(item.getPackUnit());
        response.setWholesalePrice(item.getWholesalePrice());
        response.setRetailPrice(item.getRetailPrice());
        response.setItemBrand(brandConversion(item.getItemBrand()));
        response.setStatus(item.getStatus());
        response.setCreatedBy(item.getCreatedBy());
        response.setCreatedDateTime(item.getCreatedDateTime());
        response.setModifiedBy(item.getModifiedBy());
        response.setModifiedDateTime(item.getModifiedDateTime());

        return response;
    }

    private static BrandResponse brandConversion(Brand brand) {
        BrandResponse brandResponse = new BrandResponse();

        brandResponse.setId(brand.getId());
        brandResponse.setBrandName(brand.getBrandName());

        return brandResponse;
    }

    private static CategoryResponse categoryConversion(Category category) {
        CategoryResponse categoryResponse = new CategoryResponse();

        categoryResponse.setId(category.getId());
        categoryResponse.setCategoryName(category.getCategoryName());
        categoryResponse.setCreatedBy(category.getCreatedBy());
        categoryResponse.setCreatedDateTime(category.getCreatedDateTime());
        categoryResponse.setModifiedBy(category.getModifiedBy());
        categoryResponse.setModifiedDateTime(category.getModifiedDateTime());
        categoryResponse.setStatus(category.getStatus());

        return categoryResponse;
    }
}
