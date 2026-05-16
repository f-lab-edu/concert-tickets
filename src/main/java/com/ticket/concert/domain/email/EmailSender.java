package com.ticket.concert.domain.email;

public interface EmailSender {
    void send(String mail, String content, String htmlBody);
}
