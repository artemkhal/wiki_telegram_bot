package com.khaliullov.telegrambot.repo;

import com.khaliullov.telegrambot.entity.RequestEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EntityRepository extends CrudRepository<RequestEntity, Integer> {
    List<RequestEntity> findAllByChatId(long chatId);
}
