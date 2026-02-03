package com.demo.MoneyMap.repository;

import com.demo.MoneyMap.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Client entity operations.
 * Provides CRUD operations and custom queries for client management.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Find a client by email address.
     */
    Optional<Client> findByEmail(String email);

    /**
     * Check if a client exists with the given email.
     */
    boolean existsByEmail(String email);

    /**
     * Check if a client exists with the given email, excluding a specific client ID.
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Find all active clients.
     */
    List<Client> findByActiveTrue();

    /**
     * Find all active clients with pagination.
     */
    Page<Client> findByActiveTrue(Pageable pageable);

    /**
     * Search clients by name (first name or last name).
     */
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Client> searchClients(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find client with their portfolio eagerly loaded (one-to-one).
     * CRITICAL: Uses c.portfolio (singular) not c.portfolios
     */
    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.portfolio WHERE c.id = :id")
    Optional<Client> findByIdWithPortfolios(@Param("id") Long id);

    /**
     * Count active clients.
     */
    long countByActiveTrue();
}
