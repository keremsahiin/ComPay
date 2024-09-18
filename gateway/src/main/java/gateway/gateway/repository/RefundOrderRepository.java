package gateway.gateway.repository;

import gateway.gateway.entity.RefundOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundOrderRepository extends JpaRepository<RefundOrder, Long> {
}
