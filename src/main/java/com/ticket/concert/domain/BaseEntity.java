package com.ticket.concert.domain;

import com.ticket.concert.domain.constant.Status;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseEntity {
    private LocalDateTime createdAt;
    private Long createdBy;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private Status status;
}
