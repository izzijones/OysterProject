package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

public class TravelLoggerTest {
    private static final Long peakStart = 1512630000000L; //7 am
    private static final Long peakEnd = 1512630600000L;//7:10 am

    private static final UUID cardId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private static final UUID readerId = OysterReaderLocator.atStation(Station.PADDINGTON).id();

    Customer fred = new Customer("Fred Bloggs", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));


    @Test
    public void beginJourneyRecordsTraveler() throws Exception {
        TravelLogger travelLogger = new TravelLogger();
        travelLogger.beginJourney(cardId,readerId,System.currentTimeMillis());
        assert(travelLogger.getCustomersCurrentlyTravelling().contains(cardId));
        assert(travelLogger.getEventLog().size()==1);
    }

    @Test
    public void endJourney() throws Exception {
        TravelLogger travelLogger = new TravelLogger();
        travelLogger.beginJourney(cardId,readerId,System.currentTimeMillis());
        travelLogger.endJourney(cardId,readerId,System.currentTimeMillis());
        assert(!travelLogger.getCustomersCurrentlyTravelling().contains(cardId));
        assert(travelLogger.getEventLog().size()==2);
    }

    @Test
    public void isTraveling() throws Exception {
        TravelLogger travelLogger = new TravelLogger();
        travelLogger.beginJourney(cardId,readerId,System.currentTimeMillis());
        assert(travelLogger.isTraveling(cardId));
    }


    @Test
    public void getCustomerTotal() throws Exception {
        TravelLogger travelLogger = new TravelLogger();
        travelLogger.beginJourney(cardId,readerId,peakStart);
        travelLogger.endJourney(cardId,readerId,peakEnd);
        assert(travelLogger.getCustomerTotal(travelLogger.getCustomerJourneys(travelLogger.getCustomerJourneyEvents(fred)))
                .equals(new BigDecimal(2.9)));
    }


}