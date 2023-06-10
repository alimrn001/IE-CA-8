package com.baloot.baloot.Exceptions;

public class DiscountCouponHasExpiredException extends Exception {
    public DiscountCouponHasExpiredException() {
        super("The selected coupon has been expired because it was used by you before !");
    }
}
