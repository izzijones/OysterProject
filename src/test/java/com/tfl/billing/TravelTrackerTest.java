package com.tfl.billing;

import com.oyster.OysterCardReader;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TravelTrackerTest {
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);

        private Mockery context = new JUnit4Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};

    private static final UUID readerId = OysterReaderLocator.atStation(Station.PADDINGTON).id();

    List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
    Customer customer = customers.get(0);
    private UUID cardId = customer.cardId();

    Long peakStart = 1512630000000L; //7 am
    Long peakEndShort = 1512630600000L; //7:10 am
    Long peakEndLong = 1512631800000L; // 7:30 am
    Long offPeakStart = 1512644400000L; // 11:00 am
    Long offPeakEndShort = 1512645000000L; // 11:10 am
    Long offPeakEndLong = 1512646200000L; // 11:30 am

    PaymentsSystem paymentsSystem = context.mock(PaymentsSystem.class);
    OysterCardReader paddingtonReader = context.mock(OysterCardReader.class);

    TravelLogger travelLogger = new TravelLogger();
    TravelTracker travelTracker = new TravelTracker(travelLogger);


//    Customer customer = new Customer("Fred Bloggs", new OysterCard("38400000-8cf0-11bd-b23e-10b96e4ef00d"));


    public void addJourney(Long startTime, Long endTime){
        travelLogger.beginJourney(cardId,readerId,startTime);
        travelLogger.endJourney(cardId,readerId,endTime);
    }

    @Test
    public void chargeAccountsForPeakShort() throws Exception {
        travelLogger.beginJourney(cardId,readerId,peakStart);
        travelLogger.endJourney(cardId,readerId,peakEndShort);
        List<JourneyEvent> customerJourneyEvents = travelLogger.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelLogger.getCustomerJourneys(customerJourneyEvents);
//        List<Journey> journeys = travelLogger.getCustomerJourneys(travelLogger.getCustomerJourneyEvents(customer));
        BigDecimal price = travelLogger.getCustomerTotal(customerJourneys);

        context.checking(new Expectations(){{
            atLeast(1).of(paymentsSystem).charge(customer,travelLogger.getCustomerJourneys(customerJourneyEvents),travelLogger.roundToNearestPenny(new BigDecimal(2.90)));
            
        }});

        travelTracker.chargeAccounts(paymentsSystem);
        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsForPeakLong() throws Exception{
        addJourney(peakStart,peakEndLong);

        List<Journey> journeys = travelLogger.getCustomerJourneys(travelLogger.getCustomerJourneyEvents(customer));
        BigDecimal price = travelLogger.getCustomerTotal(journeys);

        context.checking(new Expectations(){{
//            oneOf(paymentsSystem).charge(PaymentsSystem);
        }});

        travelTracker.chargeAccounts(paymentsSystem);
        context.assertIsSatisfied();
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
        TravelLogger travelLogger = new TravelLogger();
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
