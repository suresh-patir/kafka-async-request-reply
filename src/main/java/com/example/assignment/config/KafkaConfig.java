package com.example.assignment.config;


import io.confluent.examples.clients.basicavro.CustomerRequest;
import io.confluent.examples.clients.basicavro.OrderStatus;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.request.topic}")
    private String requestTopic;

    @Value("${kafka.response.topic}")
    private String requestReplyTopic;

    @Bean
    public NewTopic kRequests() {
        return TopicBuilder.name(requestTopic)
                .build();
    }

    @Bean
    public NewTopic kReplies() {
        return TopicBuilder.name(requestReplyTopic)
                .build();
    }

    public Map<String, Object> produceConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put("schema.registry.url", "http://localhost:8082");
        return configProps;
    }

    @Bean
    public ProducerFactory<String, CustomerRequest> producerFactory() {
        return new DefaultKafkaProducerFactory<>(produceConfig());
    }


    public Map<String, Object> consumerConfig() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "request-reply-group");
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configProps.put("schema.registry.url", "http://localhost:8082");
        configProps.put("specific.avro.reader", true);
        return configProps;
    }
    @Bean
    public ConsumerFactory<String, OrderStatus> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    ConcurrentMessageListenerContainer<String, OrderStatus> replyContainer(ConsumerFactory<String, OrderStatus> cf) {
        ContainerProperties containerProperties = new ContainerProperties(requestReplyTopic);
        return new ConcurrentMessageListenerContainer<>(cf, containerProperties);
    }

    @Bean
    ReplyingKafkaTemplate<String, CustomerRequest, OrderStatus> replyingKafkaTemplate(ProducerFactory<String, CustomerRequest> producerFactory,
                                                                                      ConcurrentMessageListenerContainer<String, OrderStatus> container) {
        return new ReplyingKafkaTemplate<>(producerFactory, container);
    }


    @Bean
    public ProducerFactory<String, OrderStatus> replyProducerFactory() {
        return new DefaultKafkaProducerFactory<>(produceConfig());
    }

    @Bean
    KafkaTemplate<String, OrderStatus> kafkaTemplate(){
        return new KafkaTemplate<>(replyProducerFactory());
    }

    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, OrderStatus>> kafkaListenerContainerFactory
            () {
        ConcurrentKafkaListenerContainerFactory<String, OrderStatus> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setReplyTemplate(kafkaTemplate());
        return factory;
    }

}
