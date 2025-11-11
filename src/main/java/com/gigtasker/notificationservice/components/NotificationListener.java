package com.gigtasker.notificationservice.components;

import com.gigtasker.notificationservice.config.RabbitMQConfig;
import com.gigtasker.notificationservice.dto.BidDTO;
import com.gigtasker.notificationservice.dto.BidNotificationDTO;
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
    public void handleBidAccepted(BidNotificationDTO notification) {
        log.info("Sending 'bid.accepted' notification to user: {}", notification.getBidderEmail());

        // This is the "rich" message we'll send to Angular
        String message = "You won the bid for '" + notification.getTaskTitle() + "'!";

        // This is the magic. Spring finds the WebSocket session
        // authenticated as 'notification.getBidderEmail()'
        // and sends this message *only* to them.
        messagingTemplate.convertAndSendToUser(notification.getBidderEmail(), "/queue/notify", message);
    }

    /**
     * This listener fires when a bid is rejected.
     * It sends a PRIVATE message directly to the bidder.
     */
    @RabbitListener(queues = RabbitMQConfig.BID_REJECTED_QUEUE)
    public void handleBidRejected(BidNotificationDTO notification) {
        log.info("Sending 'bid.rejected' notification to user: {}", notification.getBidderEmail());

        String message = "Your bid for '" + notification.getTaskTitle() + "' was rejected.";

        messagingTemplate.convertAndSendToUser(notification.getBidderEmail(), "/queue/notify", message);
    }
}
