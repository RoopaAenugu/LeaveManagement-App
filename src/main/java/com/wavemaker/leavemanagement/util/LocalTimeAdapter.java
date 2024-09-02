package com.wavemaker.leavemanagement.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalTimeAdapter extends TypeAdapter<LocalTime> {

    private static final DateTimeFormatter formatterWithSeconds = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter formatterWithoutSeconds = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalTime localTime) throws IOException {
        jsonWriter.value(localTime != null ? localTime.format(formatterWithSeconds) : null);
    }

    @Override
    public LocalTime read(JsonReader jsonReader) throws IOException {
        String time = jsonReader.nextString();
        if (time != null) {
            try {
                // Try parsing the time with seconds first
                return LocalTime.parse(time, formatterWithSeconds);
            } catch (DateTimeParseException e) {
                // If it fails, try parsing without seconds
                return LocalTime.parse(time, formatterWithoutSeconds);
            }
        } else {
            return null;
        }
    }
}
