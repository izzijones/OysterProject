package com.tfl.billing;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OysterCardChargeModel implements ChargeModel {
    private static final BigDecimal OFF_PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(1.60);
    private static final BigDecimal OFF_PEAK_LONG_JOURNEY_PRICE = new BigDecimal(2.70);
    private static final BigDecimal PEAK_SHORT_JOURNEY_PRICE = new BigDecimal(2.90);
    private static final BigDecimal PEAK_LONG_JOURNEY_PRICE = new BigDecimal(3.80);
    private static final BigDecimal OFF_PEAK_CAP = new BigDecimal(7.0);
    private static final BigDecimal PEAK_CAP = new BigDecimal(9.0);


    @Override
    public BigDecimal computePrice(List<Journey> journeys){
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
        return roundToNearestPenny(customerTotal);
    }

    private boolean peak(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return (hour >= 6 && hour <= 9) || (hour >= 17 && hour <= 19);
    }

    private boolean peak(Journey journey) {
        return peak(journey.startTime()) || peak(journey.endTime());
    }

    private boolean isLong(Journey journey){
        return (journey.durationSeconds() >= (25 * 60));
    }

    public static BigDecimal roundToNearestPenny(BigDecimal poundsAndPence) {
        return poundsAndPence.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
