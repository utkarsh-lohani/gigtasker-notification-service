package com.gigtasker.notificationservice.dto;

import com.gigtasker.notificationservice.enums.BidStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidDTO {
    private Long id;
    private Long taskId;
    private Long bidderUserId;
    private Double amount;
    private BidStatus status;
    private String proposal;
}
