package com.ticket.concert.domain;

import java.time.LocalDateTime;

public class BaseEntity {
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private String status;
}
