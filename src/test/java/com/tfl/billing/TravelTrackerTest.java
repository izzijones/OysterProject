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
import static com.tfl.billing.OysterCardChargeModel.roundToNearestPenny;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TravelTrackerTest {

    private Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    private static final UUID readerId = OysterReaderLocator.atStation(Station.PADDINGTON).id();

    List<Customer> customers = CustomerDatabase.getInstance().getCustomers();
    private Customer customer = customers.get(0);
    private UUID cardId = customer.cardId();

    Long peakStart = 1512626400000L; // 6:00 am
    Long peakEndShort = 1512627000000L; // 6:10 am
    Long peakEndLong = 1512628200000L; // 6:30 am

    Long offPeakStart = 1512644400000L; // 11:00 am
    Long offPeakEndShort = 1512645000000L; // 11:10 am
    Long offPeakEndLong = 1512646200000L; // 11:30 am

    PaymentsSystem paymentsSystemMock = context.mock(PaymentsSystem.class);
    OysterCardReader paddingtonReader = context.mock(OysterCardReader.class);
    TravelUtils travelUtilsMock = context.mock(TravelUtils.class);
    ChargeModel chargeModel = new OysterCardChargeModel();
    TravelUtils travelUtils = new TravelUtils();
    PaymentsSystem paymentsSystem = PaymentsSystem.getInstance();
    CustomerDatabase customerDatabase = CustomerDatabase.getInstance();
    TravelTracker travelTracker = new TravelTracker(travelUtils, paymentsSystemMock, CustomerDatabase.getInstance(), new OysterCardChargeModel());

    @Test
    public void chargeAccountsForPeakShort() throws Exception {
        travelUtils.beginJourney(cardId, readerId, peakStart); // 6:00 am
        travelUtils.endJourney(cardId, readerId, peakEndShort); // 6:10 am

        List<JourneyEvent> customerJourneyEvents = travelUtils.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelUtils.getCustomerJourneys(customerJourneyEvents);

        context.checking(new Expectations(){{
            atLeast(1).of(paymentsSystemMock).charge(customer,customerJourneys,roundToNearestPenny(new BigDecimal(2.90)));
            ignoring(paymentsSystemMock).charge(with(any(Customer.class)), with(any(List.class)), with(any(BigDecimal.class)));
        }});

        travelTracker.chargeAccounts();
        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsForPeakLong() throws Exception {
        travelUtils.beginJourney(cardId, readerId, peakStart); // 6:00 am
        travelUtils.endJourney(cardId, readerId, peakEndLong); // 6:30 am

        List<JourneyEvent> customerJourneyEvents = travelUtils.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelUtils.getCustomerJourneys(customerJourneyEvents);

        context.checking(new Expectations(){{
            atLeast(1).of(paymentsSystemMock).charge(customer, customerJourneys, roundToNearestPenny(new BigDecimal(3.80)));
            ignoring(paymentsSystemMock).charge(with(any(Customer.class)), with(any(List.class)), with(any(BigDecimal.class)));
        }});

        travelTracker.chargeAccounts();
        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsForOffPeakShort() throws Exception {
        travelUtils.beginJourney(cardId, readerId, offPeakStart); // 11:00 am
        travelUtils.endJourney(cardId, readerId, offPeakEndShort); // 11:10 am

        List<JourneyEvent> customerJourneyEvents = travelUtils.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelUtils.getCustomerJourneys(customerJourneyEvents);

        context.checking(new Expectations(){{
            atLeast(1).of(paymentsSystemMock).charge(customer, customerJourneys, roundToNearestPenny(new BigDecimal(1.60)));
            ignoring(paymentsSystemMock).charge(with(any(Customer.class)), with(any(List.class)), with(any(BigDecimal.class)));
        }});

        travelTracker.chargeAccounts();
        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsforOffPeakLong() throws Exception {
        travelUtils.beginJourney(cardId, readerId, offPeakStart); // 11:00 am
        travelUtils.endJourney(cardId, readerId, offPeakEndLong); // 11:30 am

        List<JourneyEvent> customerJourneyEvents = travelUtils.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelUtils.getCustomerJourneys(customerJourneyEvents);

        context.checking(new Expectations(){{
            atLeast(1).of(paymentsSystemMock).charge(customer, customerJourneys, roundToNearestPenny(new BigDecimal(2.70)));
            ignoring(paymentsSystemMock).charge(with(any(Customer.class)), with(any(List.class)), with(any(BigDecimal.class)));
        }});

        travelTracker.chargeAccounts();
        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsForPeakCap() throws Exception {
        travelUtils.beginJourney(cardId, readerId, peakStart); // 6:00 am
        travelUtils.endJourney(cardId, readerId, peakEndLong); // 6:30 am
        travelUtils.beginJourney(cardId, readerId, 1512628500000L); // 6:35 am
        travelUtils.endJourney(cardId, readerId, 1512630300000L); // 7:05 am
        travelUtils.beginJourney(cardId, readerId, 1512630600000L); // 7:10 am
        travelUtils.endJourney(cardId, readerId, 1512632400000L); // 7:40 am

        List<JourneyEvent> customerJourneyEvents = travelUtils.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelUtils.getCustomerJourneys(customerJourneyEvents);

        context.checking(new Expectations(){{
            atLeast(1).of(paymentsSystemMock).charge(customer, customerJourneys, roundToNearestPenny(new BigDecimal(9.00)));
            ignoring(paymentsSystemMock).charge(with(any(Customer.class)), with(any(List.class)), with(any(BigDecimal.class)));
        }});

        travelTracker.chargeAccounts();
        context.assertIsSatisfied();
    }

    @Test
    public void chargeAccountsForOffPeakCap() throws Exception {
        travelUtils.beginJourney(cardId, readerId, offPeakStart); // 11:00 am
        travelUtils.endJourney(cardId, readerId, offPeakEndLong); // 11:30 am
        travelUtils.beginJourney(cardId, readerId, 1512646500000L); // 11:35 am
        travelUtils.endJourney(cardId, readerId, 1512648300000L); // 12:05 pm
        travelUtils.beginJourney(cardId, readerId, 1512648600000L); // 12:10 pm
        travelUtils.endJourney(cardId, readerId, 1512650400000L); // 12:40 pm

        List<JourneyEvent> customerJourneyEvents = travelUtils.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelUtils.getCustomerJourneys(customerJourneyEvents);

        context.checking(new Expectations(){{
            atLeast(1).of(paymentsSystemMock).charge(customer, customerJourneys, roundToNearestPenny(new BigDecimal(7.00)));
            ignoring(paymentsSystemMock).charge(with(any(Customer.class)), with(any(List.class)), with(any(BigDecimal.class)));
        }});

        travelTracker.chargeAccounts();
        context.assertIsSatisfied();
    }

    @Test
    public void connect() throws Exception {
        TravelUtils travelUtils = new TravelUtils();
        TravelTracker travelTracker = new TravelTracker(travelUtils, paymentsSystem, customerDatabase, new OysterCardChargeModel());

        context.checking(new Expectations(){{
            exactly(1).of(paddingtonReader).register(travelTracker);
        }});

        travelTracker.connect(paddingtonReader);
        context.assertIsSatisfied();
    }

    @Test
    public void cardScanned() throws Exception {
        TravelTracker t = new TravelTracker(travelUtilsMock, paymentsSystem, customerDatabase, new OysterCardChargeModel());
        context.checking(new Expectations(){{
            exactly(1).of(travelUtilsMock).isTraveling(cardId);will(returnValue(false));
            exactly(1).of(travelUtilsMock).beginJourney(with(any(UUID.class)),with(any(UUID.class)),with(any(Long.class)));
            exactly(1).of(travelUtilsMock).isTraveling(cardId);will(returnValue(true));
            exactly(1).of(travelUtilsMock).endJourney(with(any(UUID.class)),with(any(UUID.class)),with(any(Long.class)));
        }});

        t.cardScanned(cardId,readerId);
        t.cardScanned(cardId,readerId);
        context.assertIsSatisfied();
    }

}