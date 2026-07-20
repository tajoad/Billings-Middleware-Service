package com.billings.middlewareservice.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ReferenceGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_LENGTH = 6;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Format the date using UTC timezone alignment
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMdd")
            .withZone(ZoneOffset.UTC);

    public static String generatePaymentReference() {
        // 1. Convert Instant straight to your string date component (e.g., 20260720)
        String dateComponent = DATE_FORMATTER.format(Instant.now());

        // 2. Clear red lines by explicit indexing bounds check
        StringBuilder randomSuffix = new StringBuilder(RANDOM_LENGTH);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(CHARACTERS.length());
            randomSuffix.append(CHARACTERS.charAt(randomIndex));
        }

        // Output Example: PAY-20260720-M7K3P9
        return String.format("PAY-%s-%s", dateComponent, randomSuffix);
    }
}
