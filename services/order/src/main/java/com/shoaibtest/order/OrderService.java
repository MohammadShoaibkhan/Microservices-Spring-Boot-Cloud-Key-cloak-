package com.shoaibtest.order;

import com.shoaibtest.customer.CustomerClient;
import com.shoaibtest.exception.BusinessException;
import com.shoaibtest.kafka.OrderConfirmation;
import com.shoaibtest.kafka.OrderProducer;
import com.shoaibtest.orderline.OrderLineRequest;
import com.shoaibtest.orderline.OrderLineService;
import com.shoaibtest.product.ProductClient;
import com.shoaibtest.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;

    private final OrderLineService orderLineService;

    private final OrderMapper mapper;

    private final OrderProducer orderProducer;
    public Integer createdOrder(OrderRequest request) {
        // check the customer --> OpenFeign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(()-> new BusinessException("Cannot create order:: No Customer exists with the provided ID"));

     var purchasedProducts =   this.productClient.purchaseProducts(request.products());

        var order = this.repository.save(mapper.toOrder(request));

        for (PurchaseRequest purchaseRequest: request.products()){
             orderLineService.saveOrderLine(
                     new OrderLineRequest(
                             null,
                             order.getId(),
                             purchaseRequest.productId(),
                             purchaseRequest.quantity()
                     )
             );
        }

        // todo start payment process

        // send the order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(()-> new EntityNotFoundException(String.format("No order found with the provided ID: %d",orderId)));
    }
}
