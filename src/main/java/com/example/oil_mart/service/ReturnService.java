package com.example.oil_mart.service;

import com.example.oil_mart.dto.request.ReturnReq;
import com.example.oil_mart.dto.response.ReturnRes;

import java.util.List;

public interface ReturnService {
    ReturnRes saveReturn(ReturnReq request);
    List<ReturnRes> getAllReturns();
    ReturnRes getReturnById(Long id);
    ReturnRes updateReturn(Long id, ReturnReq request);
    void deleteReturn(Long id);
    String getLastReturnNumber();
}