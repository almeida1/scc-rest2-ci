package com.fatec.scelv1.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/*
 * recupera as mensagens de erro do objeto getbindingresult retorna o responseentity
 * com uma lista de strings com o status bad_request
 */
@RestControllerAdvice
public class HandleValidationExceptions {
	Logger logger = LogManager.getLogger(HandleValidationExceptions.class);
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		logger.info(">>>>>> 1. binding result - tratamento das mensagens de erro");
		List<String> erros = ex.getBindingResult()
				.getAllErrors().stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.toList());

		return new ResponseEntity<>(erros, HttpStatus.BAD_REQUEST);
	}

}