package com.billings.middlewareservice.enums;

public enum PaymentMethod {
    CARD,          // Visa, Mastercard, Verve
    TRANSFER,      // Bank Transfer (Dynamic Virtual Accounts)
    USSD,          // *737#, etc.
    MOBILE_MONEY,  // OPay, Palmpay, MTN MoMo
    CASH           // Manual over-the-counter payments
}