package com.tfl.billing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Journey {

    private final JourneyEvent start;
    private final JourneyEvent end;
    private UUID cardId;
    private UUID startReaderId;
    private UUID endReaderId;

    public Journey(JourneyEvent start, JourneyEvent end) {
        this.start = start;
        this.end = end;
        this.cardId = start.cardId();
        this.startReaderId = start.readerId();
        this.endReaderId = end.readerId();
    }

    public UUID originId() {
        return start.readerId();
    }

    public UUID destinationId() {
        return end.readerId();
    }

    public String formattedStartTime() {
        return format(start.time());
    }

    public String formattedEndTime() {
        return format(end.time());
    }

    public Date startTime() {
        return new Date(start.time());
    }

    public Date endTime() {
        return new Date(end.time());
    }

    public int durationSeconds() {
        return (int) ((end.time() - start.time()) / 1000);
    }

    public String durationMinutes() {
        return "" + durationSeconds() / 60 + ":" + durationSeconds() % 60;
    }

    private String format(long time) {
        return SimpleDateFormat.getInstance().format(new Date(time));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journey)) return false;

        Journey journey = (Journey) o;

        if (cardId != null ? !cardId.equals(journey.cardId) : journey.cardId != null) return false;
        if (startReaderId != null ? !startReaderId.equals(journey.startReaderId) : journey.startReaderId != null)
            return false;
        return endReaderId != null ? endReaderId.equals(journey.endReaderId) : journey.endReaderId == null;
    }

    @Override
    public int hashCode() {
        int result = cardId != null ? cardId.hashCode() : 0;
        result = 31 * result + (startReaderId != null ? startReaderId.hashCode() : 0);
        result = 31 * result + (endReaderId != null ? endReaderId.hashCode() : 0);
        return result;
    }
}
