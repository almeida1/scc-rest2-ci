package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fatec.scelv1.model.Cliente;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ02ConsultarClienteJWTTests {

	@Autowired
	private TestRestTemplate testRestTemplate;
	

	// o endpoint consulta por cpf esta liberado para todos
	@Test
	public void ct01_quando_consulta_cpf_cadastrado_entao_retorna_cliente() {
		// dado que o cpf esta cadastrado
		// quando - consulta por cpf
		Cliente resposta = testRestTemplate.getForObject("/api/v1/clientes/{cpf}", Cliente.class, "11111111111");
		// entao
		assertEquals("Jose1", resposta.getNome());
	}

	// o endpoint consulta por cpf esta liberado para todos
	@Test
	public void ct02_quando_consulta_cpf_nao_cadastrado_entao_retorna_nao_cadastrado() {
		// dado que o cpf nao esta cadastrado
		// quando - consulta por cpf
		
		ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/clientes/{cpf}", String.class,"2");
		// entao
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("CPF não cadastrado", response.getBody());
	}
	// o endpoint consulta por cpf esta liberado para todos
		@Test
		public void ct03_quando_consulta_cpf_invalidoo_entao_retorna_nao_econtrado() {
			// dado que o cpf nao esta cadastrado
			// quando - consulta por cpf
			
			ResponseEntity<String> response = testRestTemplate.getForEntity("/api/v1/clientes/{cpf}", String.class,"2A");
			// entao
			assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		}
}
