package com.cadent.repository;

import com.cadent.entity.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {

    boolean existsByCrm(String crm);

    Optional<Dentista> findByCrm(String crm);

}