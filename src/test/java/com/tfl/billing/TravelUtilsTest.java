package com.tfl.billing;

import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Test;

import java.util.UUID;

public class TravelUtilsTest {
    private static final UUID cardId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private static final UUID readerId = OysterReaderLocator.atStation(Station.PADDINGTON).id();

    @Test
    public void beginJourneyRecordsTraveler() throws Exception {
        TravelUtils travelUtils = new TravelUtils();
        travelUtils.beginJourney(cardId,readerId,System.currentTimeMillis());
        assert(travelUtils.getCustomersCurrentlyTravelling().contains(cardId));
        assert(travelUtils.getEventLog().size()==1);
    }

    @Test
    public void endJourney() throws Exception {
        TravelUtils travelUtils = new TravelUtils();
        travelUtils.beginJourney(cardId,readerId,System.currentTimeMillis());
        travelUtils.endJourney(cardId,readerId,System.currentTimeMillis());
        assert(!travelUtils.getCustomersCurrentlyTravelling().contains(cardId));
        assert(travelUtils.getEventLog().size()==2);
    }

    @Test
    public void isTraveling() throws Exception {
        TravelUtils travelUtils = new TravelUtils();
        travelUtils.beginJourney(cardId,readerId,System.currentTimeMillis());
        assert(travelUtils.isTraveling(cardId));
    }


}