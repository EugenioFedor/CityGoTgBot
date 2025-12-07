import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import config.BotConfig;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GigaChatTelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final GoogleSheetsHelper googleSheetsHelper;

    public GigaChatTelegramBot(BotConfig botConfig) {
        super(botConfig.getTelegramBotToken());
        this.botConfig = botConfig;
        this.googleSheetsHelper = new GoogleSheetsHelper(botConfig);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getFirstName();

            switch (text) {
                case "/start":
                    sendWelcomeMessage(chatId, userName);
                    break;
                case "📋 Услуги":
                    showServicesMenu(chatId);
                    break;
                case "🤖 AI Рекомендация":
                    sendAiRecommendation(chatId);
                    break;
                case "📝 Анкета":
                    startQuestionnaire(chatId);
                    break;
                case "📊 Задать вопрос GigaChat":
                    sendMessage(chatId, "Введите ваш вопрос для GigaChat:");
                    break;
                case "🔙 Назад":
                    showMainMenu(chatId);
                    break;
                default:
                    // Проверяем, может это вопрос для GigaChat
                    if (text.length() > 5) {
                        processGigaChatQuestion(chatId, text);
                    } else {
                        sendMessage(chatId, "Используйте меню для навигации.");
                    }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case "service_resume":
                    sendServiceInfo(chatId, "📄 Составление резюме",
                            "Профессиональное составление резюме:\n\n" +
                                    "✅ Анализ вашего опыта\n" +
                                    "✅ Форматирование по стандартам\n" +
                                    "✅ Ключевые слова для ATS\n" +
                                    "✅ Подготовка к собеседованию\n\n" +
                                    "💰 Стоимость: 2990 руб.\n" +
                                    "⏱ Срок: 2-3 дня");
                    break;
                case "service_housing":
                    sendServiceInfo(chatId, "🏠 Подбор жилья",
                            "Полный подбор жилья:\n\n" +
                                    "✅ Поиск по вашим критериям\n" +
                                    "✅ Проверка документов\n" +
                                    "✅ Выезд на просмотр\n" +
                                    "✅ Переговоры с арендодателем\n\n" +
                                    "💰 Стоимость: 15% от аренды\n" +
                                    "⏱ Срок: 3-7 дней");
                    break;
                case "service_relocation":
                    sendServiceInfo(chatId, "🚚 Переезд под ключ",
                            "Организация переезда под ключ:\n\n" +
                                    "✅ Упаковка вещей\n" +
                                    "✅ Транспортировка\n" +
                                    "✅ Распаковка и расстановка\n" +
                                    "✅ Утилизация упаковки\n\n" +
                                    "💰 Стоимость: от 15000 руб.\n" +
                                    "⏱ Срок: 1-2 дня");
                    break;
                case "buy_service":
                    sendMessage(chatId, "Для заказа услуги свяжитесь с менеджером: @manager_username");
                    break;
                case "complete_form":
                    sendFormLink(chatId);
                    break;
            }
        }
    }

    private void sendWelcomeMessage(long chatId, String userName) {
        String welcomeText = "👋 Привет, " + userName + "!\n\n" +
                "*Добро пожаловать в сервисный бот!*\n\n" +
                "📋 *Услуги* - меню продаж\n" +
                "🤖 *AI Рекомендация* - умные советы\n" +
                "📝 *Анкета* - пройдите опрос\n" +
                "📊 *GigaChat* - задайте вопрос нейросети\n\n" +
                "Выберите раздел ниже ⬇️";

        showMainMenu(chatId, welcomeText);
    }

    private void showMainMenu(long chatId) {
        showMainMenu(chatId, "🏠 *Главное меню*");
    }

    private void showMainMenu(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setParseMode("Markdown");

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("📋 Услуги");
        row1.add("🤖 AI Рекомендация");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("📝 Анкета");
        row2.add("📊 Задать вопрос GigaChat");

        rows.add(row1);
        rows.add(row2);
        keyboard.setKeyboard(rows);

        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void showServicesMenu(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("🛒 *Меню услуг*\n\nВыберите услугу:");
        message.setParseMode("Markdown");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Резюме
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(InlineKeyboardButton.builder()
                .text("📄 Составление резюме")
                .callbackData("service_resume")
                .build());

        // Жилье
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(InlineKeyboardButton.builder()
                .text("🏠 Подбор жилья")
                .callbackData("service_housing")
                .build());

        // Переезд
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(InlineKeyboardButton.builder()
                .text("🚚 Переезд под ключ")
                .callbackData("service_relocation")
                .build());

        // Заказ
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        row4.add(InlineKeyboardButton.builder()
                .text("🛒 Заказать услугу")
                .callbackData("buy_service")
                .build());

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        keyboard.setKeyboard(rows);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendServiceInfo(long chatId, String title, String description) {
        String messageText = "*" + title + "*\n\n" +
                description + "\n\n" +
                "💰 *Стоимость:* от 2990 руб.\n" +
                "⏱ *Срок:* 2-7 дней\n" +
                "🛒 *Для заказа:* @manager_username\n" +
                "📞 *Контакты:* +7 (XXX) XXX-XX-XX";

        sendMessage(chatId, messageText, true);
    }

    private void sendAiRecommendation(long chatId) {
        String recommendation = botConfig.getAiRecommendation();
        sendMessage(chatId, recommendation, true);
    }

    private void startQuestionnaire(long chatId) {
        String formUrl = botConfig.getGoogleFormUrl();
        String chatInviteLink = botConfig.getTelegramChatInviteLink();

        String messageText = "📝 *Заполните анкету*\n\n" +
                "1. Пройдите анкету по ссылке: [Google Forms](" + formUrl + ")\n" +
                "2. После заполнения вы получите приглашение в чат\n\n" +
                "[🔗 Пройти анкету](" + formUrl + ")\n\n" +
                "После завершения: [Присоединиться к чату](" + chatInviteLink + ")";

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);
        message.setParseMode("Markdown");

        // Кнопка для отметки о завершении
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder()
                .text("✅ Я заполнил анкету")
                .callbackData("complete_form")
                .build());

        rows.add(row);
        keyboard.setKeyboard(rows);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendFormLink(long chatId) {
        String chatInviteLink = botConfig.getTelegramChatInviteLink();
        String messageText = "✅ *Спасибо за заполнение анкеты!*\n\n" +
                "Присоединяйтесь к нашему чату для общения:\n\n" +
                "[💬 Присоединиться к чату](" + chatInviteLink + ")\n\n" +
                "Также вы можете:\n" +
                "• Получить AI рекомендацию 🤖\n" +
                "• Ознакомиться с услугами 📋\n" +
                "• Задать вопрос GigaChat 📊";

        sendMessage(chatId, messageText, true);
    }

    private void processGigaChatQuestion(long chatId, String userQuestion) {
        try {
            // 1. Получаем промт из Google Sheets
            String promptFromSheets = googleSheetsHelper.readFromSheets();

            if (promptFromSheets == null || promptFromSheets.isEmpty()) {
                sendMessage(chatId, "❌ Не удалось получить данные из Google Sheets");
                return;
            }

            // 2. Объединяем с вопросом пользователя
            String fullPrompt = promptFromSheets + "\n\nВопрос пользователя: " + userQuestion;

            // 3. Отправляем в GigaChat
            String gigaChatResponse = callGigaChat(fullPrompt);

            // 4. Отправляем ответ пользователю
            sendMessage(chatId, "🤖 *Ответ GigaChat:*\n\n" + gigaChatResponse, true);

        } catch (IOException | GeneralSecurityException e) {
            sendMessage(chatId, "❌ Ошибка при обработке запроса: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String callGigaChat(String prompt) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Формируем JSON для GigaChat API
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "GigaChat");
        requestBody.addProperty("temperature", 0.7);
        requestBody.addProperty("max_tokens", 1000);

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        // Создаем массив сообщений
        com.google.gson.JsonArray messagesArray = new com.google.gson.JsonArray();
        messagesArray.add(message);
        requestBody.add("messages", messagesArray);

        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://gigachat.devices.sberbank.ru/api/v1/chat/completions")
                .post(body)
                .header("Authorization", "Bearer " + botConfig.getGigachatAccessToken())
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("GigaChat API error: " + response.code() + " - " + response.message());
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            // Извлекаем текст ответа
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }

    private void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, false);
    }

    private void sendMessage(long chatId, String text, boolean markdown) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        if (markdown) {
            message.setParseMode("Markdown");
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getTelegramBotUsername();
    }
}