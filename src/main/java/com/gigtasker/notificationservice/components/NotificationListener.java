package com.gigtasker.notificationservice.components;

import com.gigtasker.notificationservice.config.RabbitMQConfig;
import com.gigtasker.notificationservice.dto.BidDTO;
import com.gigtasker.notificationservice.dto.TaskDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * This listener fires when a new task is created.
     * It broadcasts the new task to a PUBLIC topic.
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleTaskCreated(TaskDTO task) {
        log.info("Broadcasting 'task.created' event for Task ID: {}", task.getId());

        // This broadcasts the task to everyone listening on "/topic/tasks"
        messagingTemplate.convertAndSend("/topic/tasks", task);
    }

    /**
     * This listener fires when a new bid is placed.
     * It broadcasts the bid to a DYNAMIC, task-specific topic.
     */
    @RabbitListener(queues = RabbitMQConfig.BID_QUEUE_NAME)
    public void handleBidPlaced(BidDTO bid) {
        log.info("Broadcasting 'bid.placed' event for Task ID: {}", bid.getTaskId());

        // We send it to a *dynamic* topic.
        // The "View Bids" dialog will listen to this *exact* topic.
        String destination = "/topic/task/" + bid.getTaskId() + "/bids";
        messagingTemplate.convertAndSend(destination, bid);
    }

    /**
     * This listener fires when a bid is accepted.
     * It sends a PRIVATE message directly to the bidder.
     */
    @RabbitListener(queues = RabbitMQConfig.BID_ACCEPTED_QUEUE)
    public void handleBidAccepted(BidDTO bid) {
        log.info("Sending 'bid.accepted' notification to User ID: {}", bid.getBidderUserId());

        //    This is a PRIVATE message.
        //    We need to get the user's "principal name" (their Keycloak ID/email).
        //    For now, let's assume their ID is 'user-id-string'.
        //    TODO: We need a way to map 'bid.getBidderUserId()' (our Postgres ID)
        //    to their Keycloak principal name. This is a future step.
        //    For now, we'll just log it.

        // String userPrincipalName = ... get from user-service ...
        // messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/notify", bid);

        log.warn("TODO: Need to implement User ID -> Principal mapping to send private message.");
    }

    /**
     * This listener fires when a bid is rejected.
     * It sends a PRIVATE message directly to the bidder.
     */
    @RabbitListener(queues = RabbitMQConfig.BID_REJECTED_QUEUE)
    public void handleBidRejected(BidDTO bid) {
        log.info("Sending 'bid.rejected' notification to User ID: {}", bid.getBidderUserId());

        log.warn("TODO: Need to implement User ID -> Principal mapping to send private message.");
    }
}
