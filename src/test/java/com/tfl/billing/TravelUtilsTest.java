package com.tfl.billing;

import com.oyster.OysterCard;
import com.tfl.external.Customer;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static com.tfl.billing.OysterCardChargeModel.roundToNearestPenny;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TravelUtilsTest {
    private static final Long peakStart = 1512630000000L; //7 am
    private static final Long peakEnd = 1512630600000L;//7:10 am

    private static final UUID cardId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private static final UUID readerId = OysterReaderLocator.atStation(Station.PADDINGTON).id();

    Customer fred = new Customer("Fred Bloggs", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));
    ChargeModel chargeModel = new OysterCardChargeModel();


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