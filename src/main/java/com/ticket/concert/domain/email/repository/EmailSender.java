package com.ticket.concert.domain.email.repository;

public interface EmailSender {
    void send(String mail, String content, String htmlBody);
}
