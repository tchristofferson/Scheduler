package com.tchristofferson.scheduler.util;

public class Validator {

    public static void validateDelay(long l) {
        if (isLessThanZero(l))
            throw new IllegalArgumentException("Delay cannot be < 0");
    }

    public static void validatePeriod(long l) {
        if (isLessThanZero(l))
            throw new IllegalArgumentException("Period cannot be < 0");
    }

    private static boolean isLessThanZero(long l) {
        return l < 0;
    }

}
