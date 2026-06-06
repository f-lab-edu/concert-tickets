package com.ticket.concert.domain;

import com.ticket.concert.domain.user.constant.Role;
import com.ticket.concert.domain.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void 정상_생성() {
        User user = new User(1L, "aa@aa.aa", "asdf1234!@", "이찬한", "010-0001-0001", "USER");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("aa@aa.aa");
        assertThat(user.getPassword()).isEqualTo("asdf1234!@");
        assertThat(user.getName()).isEqualTo("이찬한");
        assertThat(user.getRole()).isEqualTo(Role.USER);

    }

}