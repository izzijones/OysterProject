package com.tfl.billing;

import java.util.UUID;

public abstract class JourneyEvent {

    private final UUID cardId;
    private final UUID readerId;
    private final long time;

    public JourneyEvent(UUID cardId, UUID readerId, long time) {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = time;
    }

    public UUID cardId() {
        return cardId;
    }

    public UUID readerId() {
        return readerId;
    }

    public long time() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JourneyEvent)) return false;

        JourneyEvent that = (JourneyEvent) o;

        if (time != that.time) return false;
        if (cardId != null ? !cardId.equals(that.cardId) : that.cardId != null) return false;
        return readerId != null ? readerId.equals(that.readerId) : that.readerId == null;
    }

    @Override
    public int hashCode() {
        int result = cardId != null ? cardId.hashCode() : 0;
        result = 31 * result + (readerId != null ? readerId.hashCode() : 0);
        result = 31 * result + (int) (time ^ (time >>> 32));
        return result;
    }
}
