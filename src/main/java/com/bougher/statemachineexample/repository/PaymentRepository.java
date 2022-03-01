package com.bougher.statemachineexample.repository;

import com.bougher.statemachineexample.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
