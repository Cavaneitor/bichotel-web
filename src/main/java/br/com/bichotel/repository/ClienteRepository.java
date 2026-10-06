package br.com.bichotel.repository;

import br.com.bichotel.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByTutorContainingIgnoreCase(String tutor);
}
