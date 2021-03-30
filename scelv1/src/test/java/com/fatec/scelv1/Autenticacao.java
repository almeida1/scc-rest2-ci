package com.fatec.scelv1;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URISyntaxException;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fatec.scelv1.model.ApplicationUser;

public class Autenticacao {
	private TestRestTemplate restTemplate;

	public Autenticacao() {
		this.restTemplate = new TestRestTemplate();
	}

	public HttpHeaders login() {

		// cadastra usuario
		ApplicationUser user = new ApplicationUser();
		user.setUsername("jose");
		user.setPassword("123");
		HttpEntity<ApplicationUser> httpEntity1 = new HttpEntity<>(user);
		ResponseEntity<String> resposta1 = restTemplate.exchange("/users/sign-up", HttpMethod.POST, httpEntity1,
				String.class);

		// tenta se autenticar para obter o token
		resposta1 = restTemplate.exchange("/login", HttpMethod.POST, httpEntity1, String.class);
		assertEquals(HttpStatus.OK, resposta1.getStatusCode());

		// armazena o token no header do post
		HttpHeaders headers = resposta1.getHeaders();
		
		return headers;
	}
}
