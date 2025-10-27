package com.example.assignment.services;


import com.example.assignment.DTO.CustomerRequest;
import com.example.assignment.DTO.OrderStatus;
import jakarta.validation.Valid;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class CustomerService {


    @Value("${kafka.request.topic}")
    private String requestTopic;

    @Value("${kafka.response.topic}")
    private String requestReplyTopic;

    @Autowired
    private ReplyingKafkaTemplate<String, CustomerRequest, OrderStatus> replyingKafkaTemplate;

    public OrderStatus sendRequestAndGetStatus(@Valid @RequestBody CustomerRequest customerRequest) throws ExecutionException, InterruptedException {

        String key = UUID.randomUUID().toString();
        ProducerRecord<String, CustomerRequest> producerRecord = new ProducerRecord<>(requestTopic, key, customerRequest);

        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));

        RequestReplyFuture<String, CustomerRequest, OrderStatus> requestReplyFuture =
                replyingKafkaTemplate.sendAndReceive(producerRecord, Duration.ofSeconds(30));
        ConsumerRecord<String, OrderStatus> consumerRecord =  requestReplyFuture.get();
        return consumerRecord.value();
    }
}
