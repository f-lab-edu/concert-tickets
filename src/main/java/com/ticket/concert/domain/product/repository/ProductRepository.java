package com.ticket.concert.domain.product.repository;


import com.ticket.concert.domain.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
        SELECT p FROM Product p
        WHERE p.bookingOpenAt > :now
          AND p.deleted = false
        ORDER BY p.bookingOpenAt ASC, p.bookingCloseAt DESC
        """)
    List<Product> findUpcomingProducts(@Param("now") LocalDateTime now, Pageable pageable);
}
