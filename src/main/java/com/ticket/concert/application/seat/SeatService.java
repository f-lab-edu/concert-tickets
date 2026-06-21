package com.ticket.concert.application.seat;

import com.ticket.concert.application.dto.seat.response.SeatResponse;
import com.ticket.concert.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<SeatResponse> getSeats(Long productId) {
        return null;
    }
}
