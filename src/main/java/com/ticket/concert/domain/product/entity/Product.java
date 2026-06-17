package com.ticket.concert.domain.product.entity;

import com.ticket.concert.domain.BaseTimeEntity;
import com.ticket.concert.domain.category.entity.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "product")
public class Product extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    private String title;
    private Integer runningTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime bookingOpenAt;
    private LocalDateTime bookingCloseAt;
    private Boolean deleted;

    @Builder
    private Product(Category category, String title, Integer runningTime,
                    LocalDate startDate, LocalDate endDate,
                    LocalDateTime bookingOpenAt, LocalDateTime bookingCloseAt) {
        this.category = category;
        this.title = title;
        this.runningTime = runningTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bookingOpenAt = bookingOpenAt;
        this.bookingCloseAt = bookingCloseAt;
        this.deleted = false;
    }
}
