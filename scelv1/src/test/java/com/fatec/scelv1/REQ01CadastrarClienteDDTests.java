package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.*;

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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class REQ01CadastrarClienteDDTests {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@ParameterizedTest
    @CsvSource({
    	"jose, 123, 66666666666, Carlos, carlos@email, 03694000, 201 CREATED",       //com sucesso
    	"maria, 456, 66666666661, Carlos, carlos@email, 03694000, 201 CREATED",      //com sucesso
    	"maria1, 456, 6666666666, Carlos, carlos@email, 03694000, 400 BAD_REQUEST",  //cpf invalido 10 carac
    	"maria2, 456, 666666666655, Carlos, carlos@email, 03694000, 400 BAD_REQUEST",//cpf invalido 12 carac
    	"maria3, 456, 6, Carlos, carlos@email, 03694000, 400 BAD_REQUEST", 			//cpf invalido  1 carac
    	"maria4, 456, , Carlos, carlos@email, 03694000, 400 BAD_REQUEST", 			//cpf invalido  branco
    	"maria5, 456, 6666666666, , carlos@email, 03694000, 400 BAD_REQUEST", 		//nome invalido  branco
    	"maria6, 456, 6666666666, Carlos, , 03694000, 400 BAD_REQUEST", 			//email invalido branco
    	"maria7, 456, 6666666666, Carlos, carlos@email, 036, 400 BAD_REQUEST" 		//cep invalido  
    })
	public void ct01_validacao_do_cadastro(String id, String senha, String cpf, String nome, String email, String cep, String re) {
		//**************************************************************************************
		//dado que o usuario foi autenticado com sucesso 
		//**************************************************************************************
    	ApplicationUser user = new ApplicationUser();
    	user.setUsername(id);
    	user.setPassword(senha);
       	HttpEntity<ApplicationUser> httpEntity1 = new HttpEntity<>(user);
       	ResponseEntity<String> resposta1 = testRestTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity1, String.class);
       	assertEquals(HttpStatus.OK, resposta1.getStatusCode());
       	//tenta se autenticar para obter o token
       	resposta1 = testRestTemplate.exchange("/login", HttpMethod.POST,  httpEntity1, String.class);
    	assertEquals(HttpStatus.OK, resposta1.getStatusCode());
      	//armazena o token no header do post
    	HttpHeaders headers = resposta1.getHeaders();
    	//**************************************************************************************  	
    	//quando o usuario solicita o cadastro com o token valido
    	//**************************************************************************************
    	Cliente cliente = new Cliente(cpf, nome, email, cep);
       	HttpEntity<Cliente> httpEntity3 = new HttpEntity<>(cliente, headers);
		ResponseEntity<String> resposta2 = testRestTemplate.exchange("/api/v1/clientes", HttpMethod.POST, httpEntity3,
				String.class);
		//**************************************************************************************
		//entao valida o cadastro do cliente
		//**************************************************************************************
		assertEquals(re, resposta2.getStatusCode().toString());
		
	}
}
