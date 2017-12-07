package com.tfl.billing;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.tfl.billing.TravelTracker;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.Mockery;
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

        private Mockery context = new Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};

    private static final UUID cardId = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
    private static final UUID readerId = OysterReaderLocator.atStation(Station.PADDINGTON).id();

    Long peakStart = 1512630000000L; //7 am
    Long peakEnd = 1512630600000L;//7:10 am

    PaymentsSystem paymentsSystem = context.mock(PaymentsSystem.class);
    OysterCardReader paddingtonReader = context.mock(OysterCardReader.class);
    TravelTracker travelTracker = new TravelTracker();
    TravelLogger travelLogger = TravelLogger.getInstance();
    Customer customer = new Customer("Fred Bloggs", new OysterCard());


    @Test
    public void chargeAccountsForPeakShort() throws Exception {
        List<Journey> journeys = Arrays.asList(new Journey(new JourneyStart(cardId, readerId, peakStart),
                                                new JourneyEnd(cardId, readerId, peakEnd)));
        context.checking(new Expectations(){{
            exactly(1).of(paymentsSystem).charge(customer,journeys,PEAK_SHORT_JOURNEY_PRICE);
        }});
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

    context.checking(new Expectations(){{
        exactly(1).of(paddingtonReader).register(travelTracker);
    }});

    travelTracker.connect(paddingtonReader);
    }

    @Test
    public void cardScanned() throws Exception {

    }
}
