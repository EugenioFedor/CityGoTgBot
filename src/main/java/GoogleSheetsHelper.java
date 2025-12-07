import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import config.BotConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsHelper {

    private final BotConfig botConfig;

    public GoogleSheetsHelper(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public String readFromSheets() throws IOException, GeneralSecurityException {
        // Загружаем учетные данные
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new FileInputStream(botConfig.getGoogleCredentialsFile()))
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY));

        // Создаем сервис Sheets
        Sheets service = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("CityGo Bot")
                .build();

        // Читаем данные из таблицы
        ValueRange response = service.spreadsheets().values()
                .get(botConfig.getSpreadsheetId(), botConfig.getSheetRange())
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            return "";
        }

        // Возвращаем первый элемент
        return values.get(0).get(0).toString();
    }
}