package com.expensivebot.eventservice.service;

import com.expensivebot.eventservice.dto.event.TelegramCommandEvent;
import com.expensivebot.eventservice.dto.telegram.TelegramMessageDTO;
import com.expensivebot.eventservice.dto.telegram.TelegramUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramWebhookService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_TELEGRAM_COMMAND = "telegram.command";

    public void process(TelegramUpdateDTO update) {

        if (update.getMessage() == null || update.getMessage().getText() == null)
            return;

        String text = update.getMessage().getText();

        if (text.startsWith("/")) publishCommandEvent(update);
    }

    private void publishCommandEvent(TelegramUpdateDTO update) {
        TelegramMessageDTO message = update.getMessage();

        TelegramCommandEvent event = TelegramCommandEvent.builder()
                .chatId(message.getChat().getId())
                .userId(message.getFrom().getId())
                .messageId(message.getMessageId())
                .text(message.getText())
                .command(message.getText().split(" ")[0]) // "/gasto"
                .build();

        kafkaTemplate.send(TOPIC_TELEGRAM_COMMAND, event);
    }
}
