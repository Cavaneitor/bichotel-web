package br.com.bichotel.repository;

import br.com.bichotel.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByStatus(String status);
    List<Agendamento> findAllByOrderByDataAsc();
    boolean existsByClienteId(Long clienteId);
}
