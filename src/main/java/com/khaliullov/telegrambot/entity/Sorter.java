package com.khaliullov.telegrambot.entity;

import java.util.Comparator;

public class Sorter implements Comparator<RequestEntity> {

    @Override
    public int compare(RequestEntity o1, RequestEntity o2) {
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }
}
