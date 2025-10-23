package com.example.assignment.services;


import com.example.assignment.DTO.CustomerRequest;
import com.example.assignment.DTO.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class MockedCourierService {

    public OrderStatus getMockedOrderStatus(CustomerRequest customerRequest) {
        return new OrderStatus("packet arrived at pune city for order number : " + customerRequest.getOrderNumber());
    }
}
