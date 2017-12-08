package com.tfl.billing;

import java.math.BigDecimal;
import java.util.List;

public interface ChargeModel {
    BigDecimal computePrice(List<Journey> journeys);
}
