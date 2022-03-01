package com.bougher.statemachineexample.actions;

import com.bougher.statemachineexample.domain.PaymentEvent;
import com.bougher.statemachineexample.domain.PaymentState;
import com.bougher.statemachineexample.service.PaymentServiceImpl;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;

import java.util.Random;

public class PaymentEventActions {
    public static Action<PaymentState, PaymentEvent> preAuthAction() {
        return context -> {
            Long paymentId = (Long) context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER);

            System.out.println("PreAuth was called");

            if (new Random().nextInt(10) < 5) {
                System.out.println("Approved");
                context.getStateMachine().sendEvent(
                        MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, paymentId)
                                .build()
                );
            } else {
                System.out.println("Declined");
                context.getStateMachine().sendEvent(
                        MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, paymentId)
                                .build()
                );
            }
        };
    }

    public static Action<PaymentState, PaymentEvent> authorizeAction() {
        return context -> {
            Long paymentId = (Long) context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER);

            System.out.println("Authorize was called");

            if (new Random().nextInt(10) < 5) {
                System.out.println("Approved");
                context.getStateMachine().sendEvent(
                        MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, paymentId)
                                .build()
                );
            } else {
                System.out.println("Declined");
                context.getStateMachine().sendEvent(
                        MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, paymentId)
                                .build()
                );
            }
        };
    }
}
