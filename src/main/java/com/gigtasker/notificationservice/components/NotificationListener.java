package com.gigtasker.notificationservice.components;

import com.gigtasker.notificationservice.config.RabbitMQConfig;
import com.gigtasker.notificationservice.dto.TaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationListener {

    // Spring Boot will automatically listen to this queue.
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleTaskCreated(TaskDTO task) {
        log.info("****************************************************************");
        log.info("SUCCESS: Received new task! ID: {}, Title: {}", task.getId(), task.getTitle());
        log.info("Sending 'Welcome' email to user ID: {}", task.getPosterUserId());
        log.info("****************************************************************");
    }
}
