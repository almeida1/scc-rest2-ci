package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import com.fatec.scelv1.model.ApplicationUser;

import com.fatec.scelv1.model.Cliente;
import com.fatec.scelv1.model.ClienteRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ03AlterarClienteTests {
	Logger logger = LogManager.getLogger(REQ03AlterarClienteTests.class);
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	ClienteRepository clienteRepository;
	

	@Test
	void ct01_quando_cliente_eh_alterado_com_dados_validos_retorna_alteracao_com_sucesso() {
		// **************************************************************************************
		// dado que o usuario foi autenticado com sucesso e o cliente que sofre a alteração esta cadastrado
		// **************************************************************************************
		Cliente cliente = new Cliente("88888888888", "Carlos Jose9", "carlos_jose9@email", "03694000");
		cliente.setEndereco("Avenida Águia de Haia");
		clienteRepository.save(cliente);
		ApplicationUser user = new ApplicationUser();
		user.setUsername("antonio");
		user.setPassword("123");
		HttpEntity<ApplicationUser> httpEntity1 = new HttpEntity<>(user);
		ResponseEntity<String> resposta1 = testRestTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity1,
				String.class);
		assertEquals(HttpStatus.OK, resposta1.getStatusCode());
		// tenta se autenticar para obter o token
		resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST, httpEntity1, String.class);
		assertEquals(HttpStatus.OK, resposta1.getStatusCode());
		// armazena o token no header do post
		HttpHeaders headers = resposta1.getHeaders();
		// **************************************************************************************
		// quando o usuario confirma a alteracao com token valido
		// **************************************************************************************
		cliente = new Cliente("88888888888", "Novo Cliente", "carlos_jose9@email", "03694000");
		long id = 1;
		cliente.setId(id);
		cliente.setEndereco("Avenida Águia de Haia");
		logger.info(">>>>>> 2. Alterar cliente - Cliente alterado => "+ cliente.toString());
	    HttpEntity<Cliente> httpEntity = new HttpEntity<>(cliente, headers);
		ResponseEntity<String> resposta = testRestTemplate.exchange("/api/v1/clientes/{id}", HttpMethod.PUT,
				httpEntity, String.class, cliente.getId());
		// **************************************************************************************
		// o cliente eh alterado no banco de dados
		// **************************************************************************************
		Optional<Cliente> registro = clienteRepository.findByCpf("88888888888");
		Cliente clienteAlterado = registro.get();
		logger.info(">>>>>> 3. Alterar cliente - resultado esperado => "+ cliente.toString());
		logger.info(">>>>>>    Alterar cliente - resultado obtido   => "+ clienteAlterado.toString());
		
		assertTrue(cliente.equals(clienteAlterado));
		assertEquals("Cliente atualizado", resposta.getBody());
		assertEquals("200 OK", resposta.getStatusCode().toString());
	}

}
