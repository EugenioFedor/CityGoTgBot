package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Value("${telegram.bot.username}")
    private String telegramBotUsername;

    @Value("${google.sheet.id}")
    private String spreadsheetId;

    @Value("${google.sheet.range}")
    private String sheetRange;

    @Value("${gigachat.access.token}")
    private String gigachatAccessToken;

    @Value("${telegram.chat.invite.link}")
    private String telegramChatInviteLink;

    @Value("${google.credentials.file}")
    private String googleCredentialsFile;

    // Геттеры
    public String getTelegramBotToken() { return telegramBotToken; }
    public String getTelegramBotUsername() { return telegramBotUsername; }
    public String getSpreadsheetId() { return spreadsheetId; }
    public String getSheetRange() { return sheetRange; }
    public String getGigachatAccessToken() { return gigachatAccessToken; }
    public String getTelegramChatInviteLink() { return telegramChatInviteLink; }
    public String getGoogleCredentialsFile() { return googleCredentialsFile; }

    public String getGoogleFormUrl() {
        return "https://docs.google.com/forms/d/e/1FAIpQLSd-Cq7_WiIptvDytK6n2rAimQ2RzTFiW4HsPWsBA0N5VMgG_w/viewform";
    }

    public String getAiRecommendation() {
        return "🤖 *AI рекомендует:* На основе ваших данных советую обратить внимание на профессиональное развитие. Пройдите курсы повышения квалификации и регулярно обновляйте резюме.";
    }
}