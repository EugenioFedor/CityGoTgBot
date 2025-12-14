package ru.citygo.sheets;

import org.springframework.stereotype.Component;

@Component
public class GoogleSheetsHelper {

    public String getPrompt() {
        return "Это промт из Google Sheets (заглушка)";
    }
}
