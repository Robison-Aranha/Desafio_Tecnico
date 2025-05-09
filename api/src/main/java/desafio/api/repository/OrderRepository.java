package desafio.api.repository;

import desafio.api.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {


    @Query(value = """
    SELECT * FROM "order" o
    WHERE (:orderId IS NULL OR o.order_id = :orderId)
      AND o.date BETWEEN :start AND :end
    """, nativeQuery = true)
    Page<Order> findFilteredOrders(@Param("orderId") Integer orderId,
                                   @Param("start") LocalDate start,
                                   @Param("end") LocalDate end,
                                   Pageable pageable);


    Optional<Order> findByOrderIdAndUserName(Integer orderId, String userName);

}
