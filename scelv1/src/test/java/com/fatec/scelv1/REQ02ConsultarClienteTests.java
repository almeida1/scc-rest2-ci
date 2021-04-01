package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class REQ02ConsultarClienteTests {
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	ClienteRepository repository;
	ArrayList<Long> ids;

	public void inicializa() {
		repository.deleteAll();
		Cliente umCliente = new Cliente("66666666666", "Carlos", "carlos@email", "03694000");
		repository.save(umCliente);
		umCliente = new Cliente("77777777777", "Carlos", "carlos@email", "03694000");
		repository.save(umCliente);
		ids = new ArrayList<Long>();
		List<Cliente> clientes = repository.findAll();
		clientes.forEach(cliente -> {
			ids.add(cliente.getId());
		});
		ids.forEach(id -> System.out.println("ids validos nesta sessao=>" + id.toString()));
	}

	// acesso sem token
	@Test
	public void ct01_consultar_todos() {
		inicializa();
		ResponseEntity<Cliente[]> resposta = testRestTemplate.getForEntity("/api/v1/clientes", Cliente[].class);
		Cliente[] listaDeClientes = resposta.getBody();
		// entao
		assertEquals(2, listaDeClientes.length);

	}
	
	@Test
	public void ct02_quando_cliente_cadastrado_consulta_por_id_retorna_ok() {
		inicializa();
		Long id = ids.get(0);
		// **************************************************************************************
		// dado que o usuario foi autenticado com sucesso
		// **************************************************************************************
		ApplicationUser user = new ApplicationUser();
		user.setUsername("jose");
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
		// quando o usuario solicita a consulta por id com o token valido
		// **************************************************************************************
		HttpEntity<Cliente> httpEntity3 = new HttpEntity<>(headers);
		ResponseEntity<Cliente> resposta2 = testRestTemplate.exchange("/api/v1/clientes/" + id, HttpMethod.GET, httpEntity3,
				Cliente.class);
		assertEquals("200 OK", resposta2.getStatusCode().toString());
		
		// **************************************************************************************
		// entao valida o cadastro do cliente
		// **************************************************************************************
		Cliente cliente = resposta2.getBody();
		assertEquals("Carlos", cliente.getNome());

	}
	@ParameterizedTest
    @CsvSource({
    	"jose1, 123, 99, 404 NOT_FOUND",   
    	"jose2, 123, , 400 BAD_REQUEST",    
    	"jose3, 123,% , 403 FORBIDDEN"  // The request was rejected because the URL contained a potentially malicious String "%25"  
    
    })
	public void ct03_consulta_id_invalido_retorna_nao_encontrado(String userid, String senha, String id, String re) {
		inicializa();
		
		// **************************************************************************************
		// dado que o usuario foi autenticado com sucesso
		// **************************************************************************************
		ApplicationUser user = new ApplicationUser();
		user.setUsername(userid);
		user.setPassword(senha);
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
		// quando o usuario solicita a consulta por id com token valido
		// **************************************************************************************
		HttpEntity<Cliente> httpEntity3 = new HttpEntity<>(headers);
		ResponseEntity<Cliente> resposta2 = testRestTemplate.exchange("/api/v1/clientes/" + id, HttpMethod.GET, httpEntity3,
				Cliente.class);
		// **************************************************************************************
		// entao retorna erro
		// **************************************************************************************
		assertEquals(re, resposta2.getStatusCode().toString());
	}
	@ParameterizedTest
    @CsvSource({
    	"jose4, 123, 66666666666, 200 OK",   
    	"jose5, 123, 888888888888, 404 NOT_FOUND", 
    	"jose6, 123, 6, 404 NOT_FOUND",  
    	"jose7, 123, , 404 NOT_FOUND",  
    	"jose8, 123,% , 403 FORBIDDEN"  // The request was rejected because the URL contained a potentially malicious String "%25"  
    
    })
	public void ct04_consulta_cpf_invalido_retorn_nao_encontrado(String userid, String senha, String cpf, String re) {
		inicializa();
		
		// **************************************************************************************
		// dado que o usuario foi autenticado com sucesso
		// **************************************************************************************
		ApplicationUser user = new ApplicationUser();
		user.setUsername(userid);
		user.setPassword(senha);
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
		// quando o usuario solicita a consulta por cpf com o token valido
		// **************************************************************************************
		HttpEntity<?> httpEntity3 = new HttpEntity<>(headers);
		ResponseEntity<String> resposta2 = testRestTemplate.exchange("/api/v1/cliente/" + cpf, HttpMethod.GET, httpEntity3,
				String.class);
		//ResponseEntity<String> resposta2 = testRestTemplate.getForEntity("/api/v1/cliente/{cpf}", String.class,cpf);
		// **************************************************************************************
		// entao valida o resultado esperado
		// **************************************************************************************
		assertEquals(re, resposta2.getStatusCode().toString());
	}
}
