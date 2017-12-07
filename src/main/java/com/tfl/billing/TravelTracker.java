package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

public class TravelTracker implements ScanListener {

    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);

    static final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.0);
    static final BigDecimal PEAK_CAP = new BigDecimal(9.0);

//    private final List<JourneyEvent> eventLog = new ArrayList<>();
//    private final Set<UUID> currentlyTravelling = new HashSet<>();

    private TravelLogger travelLogger = TravelLogger.getInstance();

    public void chargeAccounts() {
        CustomerDatabase customerDatabase = CustomerDatabase.getInstance();

        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            totalJourneysFor(customer);
        }
    }

    private void totalJourneysFor(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = travelLogger.getCustomerJourneyEvents(customer);
        List<Journey> customerJourneys = travelLogger.getCustomerJourneys(customerJourneyEvents);
        BigDecimal customerTotal = travelLogger.getCustomerTotal(customerJourneys);

        PaymentsSystem.getInstance().charge(customer, customerJourneys, roundToNearestPenny(customerTotal));
    }





    private BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    private boolean isLong(Journey journey){
        return (journey.durationSeconds() >= (25 * 60));
    }


    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (!travelLogger.isTraveling(cardId)) {
            travelLogger.endJourney(cardId,readerId);
            System.out.println("Journey ended for " + cardId.toString());
        } else {
            if (CustomerDatabase.getInstance().isRegisteredId(cardId)) {
                travelLogger.beginJourney(cardId,readerId);
                System.out.println("Journey started for "+ cardId.toString());
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

}