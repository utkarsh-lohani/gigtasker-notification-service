package com.gigtasker.notificationservice.components;

import com.gigtasker.notificationservice.config.RabbitMQConfig;
import com.gigtasker.notificationservice.dto.BidDTO;
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

    @RabbitListener(queues = RabbitMQConfig.BID_QUEUE_NAME)
    public void handleBidPlaced(BidDTO bid) {
        log.info("****************************************************************");
        log.info("SUCCESS: Received new bid! Task ID: {}, Amount: {}", bid.getTaskId(), bid.getAmount());
        log.info("Sending 'You've got a new bid!' email to task poster.");
        log.info("****************************************************************");
    }

    @RabbitListener(queues = RabbitMQConfig.BID_REJECTED_QUEUE)
    public void handleBidRejected(BidDTO bid) {
        log.info("----------------------------------------------------------------");
        log.info("LOSER: Bid ID {} for ${} was REJECTED.", bid.getId(), bid.getAmount());
        log.info("Sending 'Sorry, try again' email to user ID: {}", bid.getBidderUserId());
        log.info("----------------------------------------------------------------");
    }
}
