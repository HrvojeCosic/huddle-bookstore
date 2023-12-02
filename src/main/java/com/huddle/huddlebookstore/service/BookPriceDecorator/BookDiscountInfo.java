package com.huddle.huddlebookstore.service.BookPriceDecorator;

import java.math.BigDecimal;

public record BookDiscountInfo(boolean loyaltyPointsUsed, BigDecimal discountedPrice) {

}
