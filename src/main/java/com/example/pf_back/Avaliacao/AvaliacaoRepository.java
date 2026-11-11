package com.example.pf_back.Avaliacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Imports que você pode precisar ao descomentar os métodos abaixo:
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {

    // Já vêm prontos do JpaRepository (não precisa declarar):
    // save, findById, findAll, existsById, count, delete, deleteById

    // ===== Buscas por e-mail (o Spring gera a query pelo nome do método) =====

    // Avaliações recebidas por um usuário
    // List<Avaliacao> findByEmailAvaliado(String emailAvaliado);

    // Avaliações feitas por um usuário
    // List<Avaliacao> findByEmailAvaliador(String emailAvaliador);

    // Avaliação específica de um avaliador para um avaliado
    // Optional<Avaliacao> findByEmailAvaliadorAndEmailAvaliado(String emailAvaliador, String emailAvaliado);

    // Útil para impedir avaliação duplicada no postAvaliacao
    // boolean existsByEmailAvaliadorAndEmailAvaliado(String emailAvaliador, String emailAvaliado);

    // Quantidade de avaliações recebidas
    // long countByEmailAvaliado(String emailAvaliado);

    // Apagar todas as avaliações de um usuário
    // void deleteByEmailAvaliado(String emailAvaliado);  // usar @Transactional no service

    // ===== Filtros por nota =====

    // List<Avaliacao> findByNotaGreaterThanEqual(Integer nota);
    // List<Avaliacao> findByNotaBetween(Integer min, Integer max);
    // List<Avaliacao> findByEmailAvaliadoAndNotaGreaterThanEqual(String emailAvaliado, Integer nota);

    // ===== Filtros por data =====

    // List<Avaliacao> findByDataAvaliacaoAfter(LocalDateTime data);
    // List<Avaliacao> findByDataAvaliacaoBetween(LocalDateTime inicio, LocalDateTime fim);

    // ===== Ordenação =====

    // Mais recentes primeiro
    // List<Avaliacao> findAllByOrderByDataAvaliacaoDesc();
    // List<Avaliacao> findByEmailAvaliadoOrderByDataAvaliacaoDesc(String emailAvaliado);

    // ===== Paginação =====

    // Page<Avaliacao> findByEmailAvaliado(String emailAvaliado, Pageable pageable);

    // ===== Queries customizadas (JPQL) =====

    // Média das notas recebidas por um usuário (retorna null se não tiver avaliações)
    // @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.emailAvaliado = :email")
    // Double mediaNotasPorAvaliado(@Param("email") String email);

    // Maior / menor nota recebida
    // @Query("SELECT MAX(a.nota) FROM Avaliacao a WHERE a.emailAvaliado = :email")
    // Integer maiorNotaPorAvaliado(@Param("email") String email);
    // @Query("SELECT MIN(a.nota) FROM Avaliacao a WHERE a.emailAvaliado = :email")
    // Integer menorNotaPorAvaliado(@Param("email") String email);

    // Busca ignorando maiúsculas/minúsculas no e-mail
    // List<Avaliacao> findByEmailAvaliadoIgnoreCase(String emailAvaliado);
}
