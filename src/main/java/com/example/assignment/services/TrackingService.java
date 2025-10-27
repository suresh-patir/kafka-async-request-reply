package com.example.assignment.services;


import io.confluent.examples.clients.basicavro.CustomerRequest;
import io.confluent.examples.clients.basicavro.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
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
