package com.bougher.statemachineexample.service;

import com.bougher.statemachineexample.domain.Payment;
import com.bougher.statemachineexample.domain.PaymentEvent;
import com.bougher.statemachineexample.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId);
}
