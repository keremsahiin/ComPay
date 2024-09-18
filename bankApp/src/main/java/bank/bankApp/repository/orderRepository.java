package bank.bankApp.repository;

import bank.bankApp.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface orderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(Long id);
}
