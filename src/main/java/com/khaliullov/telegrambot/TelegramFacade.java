package com.khaliullov.telegrambot;

import com.khaliullov.telegrambot.entity.RequestEntity;
import com.khaliullov.telegrambot.repo.EntityRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class TelegramFacade {

    final String START_TEXT = "Добро пожаловать!\nЭтот бот помогает восполнить пробелы в познаниях!\n"
            + "Введите интересующий вас запрос, например,  \"Спика\"";

    @Autowired
    EntityRepository repository;

    Jwiki jwiki;


    public BotApiMethod<?> handleUpdate(Update update) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        jwiki = new Jwiki(messageText);
        String responseText = jwiki.getExtractText();
        switch (messageText){
            case "/start" : {
                return sendMsg(chatId, START_TEXT, BotKeyboard.getMainMenu());
            }
            case "История запросов" : {
                String text = findResponses(chatId);
                return sendMsg(chatId, text,  null);
            }
            case "Топ 5 запросов" :  return sendMsg(chatId, getTop(), null);

        }
        if (responseText == null){
            repository.save(new RequestEntity(chatId, messageText, LocalDateTime.now(), false));
            responseText = "Запрос не найден";
        }else {
            repository.save(new RequestEntity(chatId, messageText, LocalDateTime.now(), true));
        }

        return sendMsg(chatId, responseText, null);
    }

    private String getTop() {
        int maxSize = 5;
        List<RequestEntity> requestEntities = (List<RequestEntity>) repository.findAll();
        return selectTheLargest(requestEntities, maxSize);
    }

    private String selectTheLargest(List<RequestEntity> requestEntities, int maxSize) {
        StringBuilder sb = new StringBuilder();
        Map<String, Integer> hashMap = new HashMap<>();
        for (RequestEntity entity : requestEntities){
            if (hashMap.containsKey(entity.getText())){
                int num = hashMap.get(entity.getText()) + 1;
                hashMap.put(entity.getText(), num);
            }
            else hashMap.put(entity.getText(), 1);
        }
        hashMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(maxSize)
                .forEachOrdered(e -> sb.append(e.getKey() + " - " + e.getValue() + " раз\n"));
        return sb.toString();
    }


    private String findResponses(long chatId) {
        StringBuilder sb = new StringBuilder();
        List<RequestEntity> requestEntities = repository.findAllByChatId(chatId);
        if (requestEntities.size() <= 10){
            sb.append("Всего " + requestEntities.size() + " запросов:\n");
            for (RequestEntity entity : requestEntities){
                sb.append(entity.getText() + "\n");
            }
        }else {
            sb.append("Всего " + requestEntities.size() + " запросов\n");
            sb.append("Вот 10 последних:\n");
            for (int i = requestEntities.size() -1 ; i >= requestEntities.size() - 10; i--){
                sb.append(requestEntities.get(i).getText() + "\n");
            }
        }
        return sb.toString();
    }

    SendMessage sendMsg(long chatId, String responseText, BotApiObject object){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(responseText);
        message.setReplyMarkup((ReplyKeyboard) object);
        return message;
    }

}