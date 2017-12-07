package com.tfl.billing;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class JourneyTest {
    static final UUID  cardId = UUID.randomUUID();
    static final UUID readerId1 = UUID.randomUUID();
    static final UUID readerId2 = UUID.randomUUID();
    JourneyEvent start = new JourneyStart(cardId,readerId1);
    JourneyEvent end = new JourneyEnd(cardId,readerId2);
    Journey journey = new Journey(start,end);

    @Test
    public void originId() throws Exception {
        assertThat(journey.originId(),is(readerId1));
    }

    @Test
    public void destinationId() throws Exception {
        assertThat(journey.destinationId(),is(readerId2));
    }

    @Test
    public void formattedStartTime() throws Exception {
        assertThat(journey.formattedStartTime(), is(SimpleDateFormat.getInstance().format(new Date(start.time()))));
    }

    @Test
    public void formattedEndTime() throws Exception {
    }

    @Test
    public void startTime() throws Exception {
    }

    @Test
    public void endTime() throws Exception {
    }

    @Test
    public void durationSeconds() throws Exception {
    }

    @Test
    public void durationMinutes() throws Exception {
    }

}