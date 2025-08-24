package com.example.oil_mart.controller;

import com.example.oil_mart.dto.request.ReturnReq;
import com.example.oil_mart.dto.response.ReturnRes;
import com.example.oil_mart.service.ReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("api/return")
public class ReturnController {

    @Autowired
    private ReturnService returnService;

    @PostMapping
    public ReturnRes saveReturn(@RequestBody ReturnReq returnReq){
        return returnService.saveReturn(returnReq);
    }

    @GetMapping
    public List<ReturnRes> getAllReturns(){
        return returnService.getAllReturns();
    }

    @GetMapping("/{id}")
    public ReturnRes getReturnById(@PathVariable Long id){
        return returnService.getReturnById(id);
    }

    @PutMapping("/{id}")
    public ReturnRes updateReturn(@PathVariable Long id, @RequestBody ReturnReq returnReq){
        return returnService.updateReturn(id, returnReq);
    }

    @DeleteMapping("/{id}")
    public void deleteReturn(@PathVariable Long id){
        returnService.deleteReturn(id);
    }

    @GetMapping("/last-number")
    public ResponseEntity<String> getLastReturnNumber() {
        try {
            String lastNumber = returnService.getLastReturnNumber();
            return ResponseEntity.ok(lastNumber);
        } catch (Exception e) {
            return ResponseEntity.ok("RO-0"); // Default if no returns exist
        }
    }
}