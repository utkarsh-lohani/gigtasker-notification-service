package com.gigtasker.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // These names must *exactly* match the ones in TaskService
    public static final String EXCHANGE_NAME = "task-exchange";
    public static final String ROUTING_KEY = "task.created";
    public static final String QUEUE_NAME = "notification.queue";
    public static final String BID_EXCHANGE_NAME = "bid-exchange";
    public static final String BID_ROUTING_KEY = "bid.placed";
    public static final String BID_QUEUE_NAME = "bid.notification.queue";
    public static final String BID_ACCEPTED_KEY = "bid.accepted";
    public static final String BID_REJECTED_KEY = "bid.rejected";
    public static final String BID_ACCEPTED_QUEUE = "bid.accepted.notification.queue";
    public static final String BID_REJECTED_QUEUE = "bid.rejected.notification.queue";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue queue() {
        // durable(true) means the queue survives a RabbitMQ restart
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange exchange() {
        // This just declares the exchange. If it exists, good. If not, it's created.
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        // This is the "forwarding address"
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Queue bidQueue() {
        return new Queue(BID_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange bidExchange() {
        return new TopicExchange(BID_EXCHANGE_NAME);
    }

    @Bean
    public Binding bidBinding(Queue bidQueue, TopicExchange bidExchange) {
        return BindingBuilder.bind(bidQueue)
                .to(bidExchange)
                .with(BID_ROUTING_KEY);
    }

    @Bean
    public Queue bidAcceptedQueue() {
        return new Queue(BID_ACCEPTED_QUEUE, true);
    }

    @Bean
    public Queue bidRejectedQueue() {
        return new Queue(BID_REJECTED_QUEUE, true);
    }

    @Bean
    public Binding bidAcceptedBinding() {
        return BindingBuilder.bind(bidAcceptedQueue())
                .to(bidExchange())
                .with(BID_ACCEPTED_KEY);
    }

    @Bean
    public Binding bidRejectedBinding() {
        return BindingBuilder.bind(bidRejectedQueue())
                .to(bidExchange())
                .with(BID_REJECTED_KEY);
    }
}
