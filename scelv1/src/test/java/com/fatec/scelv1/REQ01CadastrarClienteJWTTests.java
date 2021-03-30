package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.fatec.scelv1.model.ApplicationUser;
import com.fatec.scelv1.model.Cliente;
import com.fatec.scelv1.model.ClienteRepository;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ01CadastrarClienteJWTTests {

	@Autowired
	private TestRestTemplate testRestTemplate;
	private Cliente cliente;
	@Autowired
	private ClienteRepository repository;

	// o endpoint consulta por cpf esta liberado para todos
	@Test
	public void ct01_quando_consulta_todos_retorna_nove_registros() {
		// dado que nao existem registros cadastrados
		// quando - consulta 
		
    	ResponseEntity<Cliente[]> resposta = testRestTemplate.getForEntity("/api/v1/clientes", Cliente[].class);
    	Cliente[] listaDeClientes = resposta.getBody();
		// entao
		assertEquals(9, listaDeClientes.length);
	}
	@Test
	public void ct01_quando_dadosValidos_cadastra_o_cliente_com_sucesso() {
		//cadastra usuario
    	ApplicationUser user = new ApplicationUser();
    	user.setUsername("jose");
    	user.setPassword("123");
       	HttpEntity<ApplicationUser> httpEntity1 = new HttpEntity<>(user);
       	ResponseEntity<String> resposta1 = testRestTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity1, String.class);
       	assertEquals(HttpStatus.OK, resposta1.getStatusCode());
       	//tenta se autenticar para obter o token
       	resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST,  httpEntity1, String.class);
    	assertEquals(HttpStatus.OK, resposta1.getStatusCode());
      	//armazena o token no header do post
    	HttpHeaders headers = resposta1.getHeaders();
    	  	
    	//dado que as informacoes de cliente sao validas e o usuario esta autenticado
       	cliente = new Cliente("66666666666", "Carlos", "carlos@email", "03694000");
       	HttpEntity<Cliente> httpEntity3 = new HttpEntity<>(cliente, headers);
       	//quando o usuario solicita o cadastro
		ResponseEntity<String> resposta2 = testRestTemplate.exchange("/api/v1/clientes", HttpMethod.POST, httpEntity3,
				String.class);
		//entao o cliente e cadastrado com sucesso
		assertEquals(HttpStatus.CREATED, resposta2.getStatusCode());
	    repository.deleteByCpf("66666666666");

	}
	public HttpHeaders login() {

		// cadastra usuario
		ApplicationUser user = new ApplicationUser();
		user.setUsername("jose");
		user.setPassword("123");
		HttpEntity<ApplicationUser> httpEntity1 = new HttpEntity<>(user);
		ResponseEntity<String> resposta1 = testRestTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity1,
				String.class);

		// tenta se autenticar para obter o token
		resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST, httpEntity1, String.class);
		assertEquals(HttpStatus.OK, resposta1.getStatusCode());

		// armazena o token no header do post
		HttpHeaders headers = resposta1.getHeaders();
		
		return headers;
	}
}
