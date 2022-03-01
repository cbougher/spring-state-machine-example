package com.bougher.statemachineexample.service;

import com.bougher.statemachineexample.domain.Payment;
import com.bougher.statemachineexample.domain.PaymentEvent;
import com.bougher.statemachineexample.domain.PaymentState;
import com.bougher.statemachineexample.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository repository;
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final PaymentStateChangeInterceptor interceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);

        return repository.save(payment);
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> machine = build(paymentId);

        sendEvent(paymentId, machine, PaymentEvent.PRE_AUTHORIZE);

        return machine;
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> machine = build(paymentId);

        sendEvent(paymentId, machine, PaymentEvent.AUTHORIZE);

        return machine;
    }

    private void sendEvent(Long id, StateMachine<PaymentState, PaymentEvent> machine, PaymentEvent event) {
        Message<PaymentEvent> message = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, id)
                .build();

        machine.sendEvent(message);
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long id) {
        Optional<Payment> paymentOptional = repository.findById(id);

        if (paymentOptional.isEmpty()) {
            throw new RuntimeException("Invalid payment id");
        }

        Payment payment = paymentOptional.get();
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(Long.toString(payment.getId()));

        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(interceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null, null, null));
                });
        sm.start();

        return sm;
    }
}
