package com.shoaibtest.kafka;

import com.shoaibtest.customer.CustomerResponse;
import com.shoaibtest.order.PaymentMethod;
import com.shoaibtest.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(

        String orderReference,

        BigDecimal totalAmount,

        PaymentMethod paymentMethod,

        CustomerResponse customer,

        List<PurchaseResponse> products


) {
}
