package ru.citygo.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.citygo.config.BotConfig;
import ru.citygo.sheets.GoogleSheetsHelper;

@Component
public class GigaChatTelegramBot extends TelegramWebhookBot {

    private final BotConfig config;
    private final GoogleSheetsHelper sheetsHelper;

    public GigaChatTelegramBot(BotConfig config, GoogleSheetsHelper sheetsHelper) {
        super();
        this.config = config;
        this.sheetsHelper = sheetsHelper;
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotPath() {
        return "/webhook";
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return null;

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        String reply;
        if ("/start".equals(text)) {
            reply = "👋 Добро пожаловать в CityGo!";
        } else if ("получить рекомендацию".equalsIgnoreCase(text)) {
            reply = "🤖 Рекомендация: используйте наши услуги!";
        } else {
            // заглушка Google Sheets
            String prompt = sheetsHelper.getPrompt();
            reply = "📄 Промт из Google Sheets:\n\n" + prompt;
        }

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(reply)
                .build();

        return message; // возвращаем объект BotApiMethod<?>
    }
}
