package com.ticket.concert.presentation;

import com.ticket.concert.application.dto.user.request.JoinRequest;
import com.ticket.concert.global.common.ApiResponse;
import com.ticket.concert.application.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/v1/user/join")
    public ApiResponse<Void> join(@Valid @RequestBody JoinRequest request) {
        userService.join(request);
        return ApiResponse.success();
    }
}
