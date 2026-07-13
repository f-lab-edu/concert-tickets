package com.ticket.concert.application.seatInventory;

import com.ticket.concert.application.dto.seatInventory.request.SeatInventoryRequest;
import com.ticket.concert.application.dto.seatInventory.response.SeatInventoryResponse;
import com.ticket.concert.domain.saetInventory.repository.SeatInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeatInventoryService {

    private final SeatInventoryRepository seatInventoryRepository;

    public List<SeatInventoryResponse> getSeatInventory(SeatInventoryRequest request) {
        return seatInventoryRepository.findBySeatIdAndPerformanceIdAndDeleted(request.performanceId(), false);
    }
}
