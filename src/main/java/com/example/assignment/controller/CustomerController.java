package com.example.assignment.controller;


import com.example.assignment.DTO.CustomerRequest;
import com.example.assignment.DTO.OrderStatus;
import com.example.assignment.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping(path = "/orderStatus")
    public OrderStatus getOrderStatus(@Valid @RequestBody CustomerRequest customerRequest) throws ExecutionException, InterruptedException {
        OrderStatus orderStatus = customerService.sendRequestAndGetStatus(customerRequest);
        System.out.println(orderStatus.getStatusMessage());
        return orderStatus;
    }
}
