package com.bougher.statemachineexample.service;

import com.bougher.statemachineexample.domain.Payment;
import com.bougher.statemachineexample.domain.PaymentEvent;
import com.bougher.statemachineexample.domain.PaymentState;
import com.bougher.statemachineexample.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository repository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Test
    @Transactional
    void preAuth() {
        Payment savedPayment = paymentService.newPayment(payment);

        System.out.println("Should be NEW");
        System.out.println(savedPayment.getState());

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());

        Payment preAuthPayment = repository.getOne(savedPayment.getId());

        System.out.println("Should be PRE_AUTH or PRE_AUTH_ERROR");
        System.out.println(sm.getState().getId());

        System.out.println(preAuthPayment);
    }

    @Test
    @Transactional
    @RepeatedTest(10)
    void authorize() {
        Payment savedPayment = paymentService.newPayment(payment);

        StateMachine<PaymentState, PaymentEvent> pasm = paymentService.preAuth(savedPayment.getId());

        if (pasm.getState().getId() == PaymentState.PRE_AUTH) {
            System.out.println("Payment is pre-authorized");

            StateMachine<PaymentState, PaymentEvent> asm = paymentService.authorize(savedPayment.getId());

            System.out.println("Result of Auth: " + asm.getState().getId());
        } else {
            System.out.println("Payment pre-auth failed: " + pasm.getState().getId());
        }
    }
}