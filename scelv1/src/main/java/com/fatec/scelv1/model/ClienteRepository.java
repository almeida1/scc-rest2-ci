package com.fatec.scelv1.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	Optional<Cliente> findByCpf(String cpf);
	Optional<Cliente> findById(Long id);
	@Query("DELETE FROM Cliente WHERE cpf = :cpf")
	void deleteByCpf(@Param("cpf") String cpf);
}
