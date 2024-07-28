package com.shoaibtest.kafka;

import com.shoaibtest.email.EmailService;
import com.shoaibtest.kafka.order.OrderConfirmation;
import com.shoaibtest.kafka.payment.PaymentConfirmation;
import com.shoaibtest.notification.Notification;
import com.shoaibtest.notification.NotificationRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.shoaibtest.notification.NotificationType.ORDER_CONFIRMATION;
import static com.shoaibtest.notification.NotificationType.PAYMENT_CONFIRMATION;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

       private final NotificationRepository repository;
       private final EmailService emailService;
       @KafkaListener(topics = "payment-topic")
       public void  consumePaymentSuccessNotification(PaymentConfirmation paymentConfirmation) throws MessagingException {
          log.info(format("Consuming the message from payment-topics Topic:: %s",paymentConfirmation));
           repository.save(
                   Notification.builder()
                           .type(PAYMENT_CONFIRMATION)
                           .notificationDate(LocalDateTime.now())
                           .paymentConfirmation(paymentConfirmation)
                           .build()
           ) ;
           //  send email
           var customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
           emailService.sentPaymentSuccessEmail(
                   paymentConfirmation.customerEmail(),
                   customerName,
                   paymentConfirmation.amount(),
                   paymentConfirmation.orderReference()
           );
      }

    @KafkaListener(topics = "order-topic")
    public void  consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) throws MessagingException {
        log.info(String.format("Consuming the message from order-topics Topic:: %s",orderConfirmation));
        repository.save(
                Notification.builder()
                        .type(ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        ) ;
        //  send email
        var customerName = orderConfirmation.customer().firstname() + " " + orderConfirmation.customer().lastname();
        emailService.sentOderConfirmationEmail(
                orderConfirmation.customer().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderReference(),
                orderConfirmation.products()
        );
    }
}
