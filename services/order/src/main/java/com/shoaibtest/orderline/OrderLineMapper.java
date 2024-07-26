package com.shoaibtest.orderline;

import com.shoaibtest.order.Order;
import org.springframework.stereotype.Service;

import static com.shoaibtest.orderline.OrderLine.*;

@Service
public class OrderLineMapper {

    public OrderLine toOrderLine(OrderLineRequest request) {
        return OrderLine.builder()
                .id(request.id())
                .quantity(request.quantity())
               .order(
                       Order.builder()
                        .id(request.orderId())
                       .build()
               )
                .productId(request.productId())
               .build();

    }

    public OrderLineResponse toOrderLineResponse(OrderLine orderLine) {
    return new OrderLineResponse(orderLine.getId(), orderLine.getQuantity());
    }
}
