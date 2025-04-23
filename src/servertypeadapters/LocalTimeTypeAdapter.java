package servertypeadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalTimeTypeAdapter extends TypeAdapter<LocalDateTime> {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yy.MM.dd|HH:mm:ss");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDateTime.format(dateTimeFormatter));
        }
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        try {
            return LocalDateTime.parse(jsonReader.nextString(), dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new IOException("Недопустимый формат: " + jsonReader.nextString(), e);
        }
    }
}
