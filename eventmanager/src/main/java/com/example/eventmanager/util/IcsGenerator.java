package com.example.eventmanager.util;

import com.example.eventmanager.model.Event;

import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class IcsGenerator {

    public static String generateIcsFile(Event event) throws IOException {
        String fileName = "event_" + event.getId() + ".ics";
        StringBuilder sb = new StringBuilder();

        String dtStart = event.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
        String dtEnd = event.getDate().plusHours(2).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));

        sb.append("BEGIN:VCALENDAR\n");
        sb.append("VERSION:2.0\n");
        sb.append("PRODID:-//EventManager//iCal4j 1.0//EN\n");
        sb.append("BEGIN:VEVENT\n");
        sb.append("UID:event-" + event.getId() + "@eventmanager.com\n");
        sb.append("DTSTAMP:" + dtStart + "Z\n");
        sb.append("DTSTART:" + dtStart + "Z\n");
        sb.append("DTEND:" + dtEnd + "Z\n");
        sb.append("SUMMARY:" + event.getTitle() + "\n");
        sb.append("DESCRIPTION:" + event.getDescription() + "\n");
        sb.append("END:VEVENT\n");
        sb.append("END:VCALENDAR");

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(sb.toString());
        }

        return fileName;
    }
}