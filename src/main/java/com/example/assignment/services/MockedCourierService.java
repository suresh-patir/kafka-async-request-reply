package com.example.assignment.services;


import io.confluent.examples.clients.basicavro.CustomerRequest;
import io.confluent.examples.clients.basicavro.OrderStatus;
import org.springframework.stereotype.Service;

@Service
public class MockedCourierService {

    public OrderStatus getMockedOrderStatus(CustomerRequest customerRequest) {
        return new OrderStatus("packet arrived at pune city for order number : " + customerRequest.getOrderNumber());
    }
}
