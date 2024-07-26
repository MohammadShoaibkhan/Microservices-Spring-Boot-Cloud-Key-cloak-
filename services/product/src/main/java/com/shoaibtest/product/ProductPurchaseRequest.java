package com.shoaibtest.product;

import jakarta.validation.constraints.NotNull;

public record ProductPurchaseRequest(

        @NotNull(message = "Product is mandatory")
        Integer productId,
        @NotNull(message = "Category is mandatory")
        double quantity

) {
}
