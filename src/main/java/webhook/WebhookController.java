package ru.citygo.webhook;

import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.citygo.bot.GigaChatTelegramBot;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final GigaChatTelegramBot bot;

    public WebhookController(GigaChatTelegramBot bot) {
        this.bot = bot;
    }

    @PostMapping
    public void onUpdate(@RequestBody Update update) {
        bot.onWebhookUpdateReceived(update);
    }
}
