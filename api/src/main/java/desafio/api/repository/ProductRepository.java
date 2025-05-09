package desafio.api.repository;

import desafio.api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductIdAndValueAndOrderOrderIdAndOrderUserName(Integer id, Double value, Integer orderId, String userName);
}
