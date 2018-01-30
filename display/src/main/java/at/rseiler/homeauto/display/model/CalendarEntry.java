package at.rseiler.homeauto.display.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarEntry {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final String[] rawValues;
    private final String title;
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final String room;
    private final String organiser;

    public CalendarEntry(String[] rawValues, String title, LocalDateTime from, LocalDateTime to, String room, String organiser) {
        this.rawValues = rawValues;
        this.title = title;
        this.from = from;
        this.to = to;
        this.room = room;
        this.organiser = organiser;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public String getRoom() {
        return room;
    }

    public String getOrganiser() {
        return organiser;
    }

    public String getOrganiserShort() {
        if(organiser.isEmpty()) {
            return "Reinhard S.";
        }

        String[] split = organiser.split(" ", 2);

        if (split.length == 2) {
            return split[0] + " " + split[1].substring(0, 1) + ".";
        }

        return organiser;
    }

    public String getDisplayTime() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        if (from.toLocalDate().isEqual(today) && from.getHour() == 0 && to.toLocalDate().isEqual(tomorrow) && to.getHour() == 0) {
            return "heute";
        }

        return (from.toLocalDate().isBefore(today) ? "#####" : TIME_FORMATTER.format(from)) + " - " + (to.toLocalDate().isAfter(today) ? "#####" : TIME_FORMATTER.format(to));
    }

    @Override
    public String toString() {
        return "CalendarEntry{" +
                "title='" + title + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", room='" + room + '\'' +
                ", organiser='" + organiser + '\'' +
                '}';
    }
}
