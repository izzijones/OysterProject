package com.tfl.billing;

import com.tfl.billing.TravelTracker;
import com.tfl.external.Customer;
import com.tfl.external.PaymentsSystem;
import org.jmock.Expectations;
import org.junit.Test;
import org.jmock.integration.junit4.JUnitRuleMockery;

public class TravelTrackerTest {

    JUnitRuleMockery context = new JUnitRuleMockery();
    PaymentsSystem paymentsSystem = context.mock(PaymentsSystem.class);

    @Test
    public void chargeAccountsForPeakShort() throws Exception {

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
    public void connect() throws Exception {

    }

    @Test
    public void cardScanned() throws Exception {

    }
}
