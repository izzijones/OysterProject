package com.tfl.billing;

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

public class TravelTrackerTest {

        private Mockery context = new Mockery() {{
            setImposteriser(ClassImposteriser.INSTANCE);
        }};

    PaymentsSystem paymentsSystem = context.mock(PaymentsSystem.class);
    OysterCardReader oysterCardReader = OysterReaderLocator.atStation(Station.PADDINGTON);

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
    public void chargeAccountsForOffPeakCap() throws Exception{

    }

    @Test
    public void chargeAccountsForPeakCap() throws Exception{

    }

    @Test
    public void connect() throws Exception {
//        context.checking(new Expectations(){{
//            exactly(1).of()
//        }});

    }

    @Test
    public void cardScanned() throws Exception {

    }
}
