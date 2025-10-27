package com.example.assignment.controller;


import com.example.assignment.services.CustomerService;
import io.confluent.examples.clients.basicavro.CustomerRequest;
import io.confluent.examples.clients.basicavro.OrderStatus;
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
    public String getOrderStatus(@Valid @RequestBody CustomerRequest customerRequest) throws ExecutionException, InterruptedException {
        OrderStatus orderStatus = customerService.sendRequestAndGetStatus(customerRequest);
        System.out.println(orderStatus.getStatusMessage());
        return orderStatus.getStatusMessage();
    }
}
