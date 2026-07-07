package com.ticket.concert.domain.seat.entity;

import com.ticket.concert.domain.BaseTimeEntity;
import com.ticket.concert.domain.product.entity.Product;
import jakarta.persistence.Column;
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

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "seat")
public class Seat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    private String zone;
    @Column(name = "seat_row")
    private String row;
    private String seatNo;
    private String grade;
    private Integer price;
    private String color;
    private Boolean deleted;

    @Builder
    public Seat(Product product, String zone, String row, String seatNo, Integer price, String grade, String color) {
        this.product = product;
        this.zone = zone;
        this.row = row;
        this.seatNo = seatNo;
        this.price = price;
        this.grade = grade;
        this.color = color;
        this.deleted = false;
    }
}
