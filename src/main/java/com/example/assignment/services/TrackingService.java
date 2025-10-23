package com.example.assignment.services;


import com.example.assignment.DTO.CustomerRequest;
import com.example.assignment.DTO.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {

    @Autowired
    private MockedCourierService mockedCourierService;

    @KafkaListener(groupId = "request-reply-group", topics = "request-topic-new")
    @SendTo
    public OrderStatus computeOrderStatus(CustomerRequest customerRequest) {
        return mockedCourierService.getMockedOrderStatus(customerRequest);
    }
}
