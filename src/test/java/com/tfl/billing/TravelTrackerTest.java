package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.TravelTracker;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Expectation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;

import java.math.BigDecimal;
import java.util.*;

public class TravelTrackerTest {
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);

        private Mockery context = new JUnit4Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};

    private static final UUID cardId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private static final UUID readerId = OysterReaderLocator.atStation(Station.PADDINGTON).id();

//    CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
    Customer fred = new Customer("Fred Bloggs", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));

    Long peakStart = 1512630000000L; //7 am
    Long peakEnd = 1512630600000L;//7:10 am

    PaymentsSystem paymentsSystem = context.mock(PaymentsSystem.class);
    OysterCardReader paddingtonReader = context.mock(OysterCardReader.class);

    TravelLogger travelLogger = TravelLogger.getInstance();


//    Customer customer = new Customer("Fred Bloggs", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));


    @Test
    public void chargeAccountsForPeakShort() throws Exception {
        TravelTracker travelTracker = new TravelTracker(travelLogger);
        travelLogger.beginJourney(cardId, readerId, peakStart);
        travelLogger.endJourney(cardId,readerId, peakEnd);

        List<Journey> journeys = travelLogger.getCustomerJourneys(travelLogger.getCustomerJourneyEvents(fred));
        context.checking(new Expectations(){{
            exactly(1).of(paymentsSystem).charge(fred,journeys,PEAK_SHORT_JOURNEY_PRICE);
        }});

        travelTracker.chargeAccounts();
        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsForPeakLong() throws Exception{

    }

    @Test
    public void chargeAccountsforOffPeakLong() throws Exception{

    }

    @Test
    public void chargeAccountsForOffPeakShort() throws Exception{

    }

    @Test
    public void chargeAccountsForOffPeakCap() throws Exception{

    }

    @Test
    public void chargeAccountsForPeakCap() throws Exception{

    }

    @Test
    public void connect() throws Exception {
        TravelTracker travelTracker = new TravelTracker(travelLogger);

        context.checking(new Expectations(){{
            exactly(1).of(paddingtonReader).register(travelTracker);
        }});

        travelTracker.connect(paddingtonReader);
        context.assertIsSatisfied();
    }


    @Test
    public void cardScanned() throws Exception {

    }
}
