package com.shoaibtest.payment;

import com.shoaibtest.customer.CustomerResponse;
import com.shoaibtest.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(

        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer

) {
}
