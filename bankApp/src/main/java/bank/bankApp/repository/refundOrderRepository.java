package bank.bankApp.repository;

import bank.bankApp.entity.RefundOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface refundOrderRepository extends JpaRepository<RefundOrder , Long> {

}
