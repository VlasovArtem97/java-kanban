package servertypeadapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(String.valueOf(duration.toMinutes()));
        }
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        try {
            return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
        } catch (NumberFormatException e) {
            throw new IOException("Недопустимый формат: " + jsonReader.nextString(), e);
        }
    }
}
