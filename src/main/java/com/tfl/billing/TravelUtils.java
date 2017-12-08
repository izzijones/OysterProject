package com.tfl.billing;

import com.tfl.external.Customer;

import java.util.*;

public class TravelUtils {

    private final List<JourneyEvent> eventLog = new ArrayList<>();
    private final Set<UUID> currentlyTravelling = new HashSet<>();

    public TravelUtils(){
    }


    public void beginJourney(UUID cardId, UUID readerId, long time){
        currentlyTravelling.add(cardId);
        eventLog.add(new JourneyStart(cardId,readerId, time) {
        });
    }

    public void endJourney(UUID cardId, UUID readerId, long time){
        currentlyTravelling.remove(cardId);
        eventLog.add(new JourneyEnd(cardId,readerId, time));
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


    public Set<UUID> getCustomersCurrentlyTravelling(){
        return currentlyTravelling;
    }
    public List<JourneyEvent> getEventLog(){
        return eventLog;
    }


}
