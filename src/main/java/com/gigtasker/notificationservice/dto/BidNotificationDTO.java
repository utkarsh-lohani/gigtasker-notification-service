package com.gigtasker.notificationservice.dto;

import lombok.Data;

@Data
public class BidNotificationDTO {
    private Double amount;
    private String bidderEmail;
    private String taskTitle;
}
