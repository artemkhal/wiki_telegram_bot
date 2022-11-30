package com.khaliullov.telegrambot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Comparator;


@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Getter
public class RequestEntity implements Comparator<RequestEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    long chatId;
    String text;
    LocalDateTime timestamp;
    boolean hasResponse;

    public RequestEntity(long chatId, String text, LocalDateTime timestamp, boolean hasResponse) {
        this.chatId = chatId;
        this.text = text;
        this.timestamp = timestamp;
        this.hasResponse = hasResponse;
    }

    @Override
    public int compare(RequestEntity o1, RequestEntity o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}
