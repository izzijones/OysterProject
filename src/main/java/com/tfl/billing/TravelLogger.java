package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

public class TravelLogger {
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);

    static final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.0);
    static final BigDecimal PEAK_CAP = new BigDecimal(9.0);
    private final List<JourneyEvent> eventLog = new ArrayList<>();
    private final Set<UUID> currentlyTravelling = new HashSet<>();
    private static TravelLogger instance = new TravelLogger();

    private TravelLogger(){}

    public static TravelLogger getInstance(){
        return instance;
    }

    public void beginJourney(UUID cardId, UUID readerId){
        currentlyTravelling.add(cardId);
        eventLog.add(new JourneyStart(cardId,readerId, System.currentTimeMillis()) {
        });
    }

    public void endJourney(UUID cardId, UUID readerId){
        currentlyTravelling.remove(cardId);
        eventLog.add(new JourneyEnd(cardId,readerId, System.currentTimeMillis()));
    }

    public boolean isTraveling(UUID cardId){
        return currentlyTravelling.contains(cardId);
    }


    public List<JourneyEvent> getCustomerJourneyEvents(Customer customer) {
        List<JourneyEvent> customerJourneyEvents = new ArrayList<>();
        for (JourneyEvent journeyEvent : getEventLog()) {
            if (journeyEvent.cardId().equals(customer.cardId())) {
                customerJourneyEvents.add(journeyEvent);
            }
        }
        return customerJourneyEvents;
    }

    public List<Journey> getCustomerJourneys(List<JourneyEvent> customerJourneyEvents) {
        List<Journey> journeys = new ArrayList<>();

        JourneyEvent start = null;
        for (JourneyEvent event : customerJourneyEvents) {
            if (event instanceof JourneyStart) {
                start = event;
            }
            if (event instanceof JourneyEnd && start != null) {
                journeys.add(new Journey(start, event));
                start = null;
            }
        }
        return journeys;
    }


    public BigDecimal getCustomerTotal(List<Journey> journeys) {
        BigDecimal customerTotal = new BigDecimal(0);
        int offPeakCount = 0;

        for (Journey journey : journeys) {

            BigDecimal journeyPrice;

            if (peak(journey) && isLong(journey)) {
                journeyPrice = PEAK_LONG_JOURNEY_PRICE;
            }
            else if (peak(journey) && ! isLong(journey)){
                journeyPrice = PEAK_SHORT_JOURNEY_PRICE;
            }
            else if (! peak(journey) && isLong(journey)){
                journeyPrice = OFF_PEAK_LONG_JOURNEY_PRICE;
                offPeakCount++;
            }
            else {
                journeyPrice = OFF_PEAK_SHORT_JOURNEY_PRICE;
                offPeakCount++;
            }

            customerTotal = customerTotal.add(journeyPrice);


        }
        if (journeys.size() == offPeakCount && (customerTotal.compareTo(OFF_PEAK_CAP) > 0)){
            customerTotal = OFF_PEAK_CAP;
        }
        else if (customerTotal.compareTo(PEAK_CAP) > 0){
            customerTotal = PEAK_CAP;
        }
        return customerTotal;
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

    public Set<UUID> getCustomersCurrentlyTravelling(){
        return currentlyTravelling;
    }
    public List<JourneyEvent> getEventLog(){
        return eventLog;
    }


}
