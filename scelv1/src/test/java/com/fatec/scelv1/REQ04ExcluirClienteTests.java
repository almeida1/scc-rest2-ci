package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
class REQ04ExcluirClienteTests {

	Logger logger = LogManager.getLogger(REQ04ExcluirClienteTests.class);
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	ClienteRepository clienteRepository;
	
	@ParameterizedTest
    @CsvSource({
    	"jose, 123, 1, 200 OK",       //com sucesso
    	"jose1, 123, 0, 404 NOT_FOUND"       //com sucesso
    })
	void ct01_quando_cliente_cadastrado_com_dados_validos_retorna_excluido_com_sucesso(String userId, String senha, Long i, String re) {
		// **************************************************************************************
		// dado que o usuario foi autenticado com sucesso e o cliente que sofre a exclusao esta cadastrado
		// **************************************************************************************
		Cliente cliente = new Cliente("88888888889", "Carlos Jose9", "carlos_jose9@email", "03694000");
		cliente.setEndereco("Avenida Águia de Haia");
		clienteRepository.save(cliente);
		ApplicationUser user = new ApplicationUser();
		user.setUsername(userId);
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
		// quando o usuario confirma a exclusao com token valido
		// **************************************************************************************
		 HttpEntity<Cliente> httpEntity = new HttpEntity<>(headers);		
		ResponseEntity<Cliente> resposta = testRestTemplate.exchange("/api/v1/clientes/{id}", HttpMethod.DELETE, httpEntity,
				Cliente.class, 1);
		// **************************************************************************************
		// entao na consulta retorna nao localizado
		// **************************************************************************************
		long id = i;
		Optional<Cliente> registro = clienteRepository.findById(id);
		assertTrue(registro.isEmpty());
		assertEquals(re, resposta.getStatusCode().toString());
	}


}
