package com.gigtasker.notificationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // These are the "broadcast" topics.
        // The Angular app will subscribe to these.
        // /topic/bids -> A public feed of all bids
        // /user/queue/notify -> A *private* feed for a specific user
        registry.enableSimpleBroker("/topic", "/user");

        // This is the "inbox" prefix.
        // If Angular sends a message *to* the server (which we're not doing yet),
        // it would send it to "/app/hello", for example.
        registry.setApplicationDestinationPrefixes("/app");

        // This is the magic prefix for sending one-to-one messages.
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the "front door" for your Angular app.
        // It will connect to "http://localhost:????/ws"
        registry.addEndpoint("/ws")
                // We must allow our Angular app (on :4200) to connect
                .setAllowedOrigins("http://localhost:4200");
    }
}
