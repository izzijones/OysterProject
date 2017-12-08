package com.tfl.billing;

import com.oyster.*;
import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;
import com.tfl.external.PaymentsSystem;

import java.math.BigDecimal;
import java.util.*;

public class TravelTracker implements ScanListener {


    private TravelUtils travelUtils;
    private PaymentsSystem paymentsSystem;
    private CustomerDatabase customerDatabase;
    private ChargeModel chargeModel;

    public TravelTracker(TravelUtils travelUtils, PaymentsSystem paymentsSystem, CustomerDatabase customerDatabase, ChargeModel chargeModel){
        this.travelUtils = travelUtils;
        this.paymentsSystem = paymentsSystem;
        this.customerDatabase = customerDatabase;
        this.chargeModel = chargeModel;
    }

    public void chargeAccounts() {

        List<Customer> customers = customerDatabase.getCustomers();
        for (Customer customer : customers) {
            List<JourneyEvent> customerJourneyEvents = travelUtils.getCustomerJourneyEvents(customer);
            List<Journey> customerJourneys = travelUtils.getCustomerJourneys(customerJourneyEvents);
            BigDecimal customerTotal = chargeModel.computePrice(customerJourneys);

            paymentsSystem.charge(customer, customerJourneys, customerTotal);
        }
    }

    public void connect(OysterCardReader... cardReaders) {
        for (OysterCardReader cardReader : cardReaders) {
            cardReader.register(this);
        }
    }

    @Override
    public void cardScanned(UUID cardId, UUID readerId) {
        if (travelUtils.isTraveling(cardId)) {
            travelUtils.endJourney(cardId,readerId, System.currentTimeMillis());
            System.out.println("Journey ended for " + cardId.toString());
        } else {
            if (CustomerDatabase.getInstance().isRegisteredId(cardId)) {
                travelUtils.beginJourney(cardId,readerId, System.currentTimeMillis());
                System.out.println("Journey started for "+ cardId.toString());
            } else {
                throw new UnknownOysterCardException(cardId);
            }
        }
    }

}