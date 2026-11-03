package com.curso.springai.financiero.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/** Ollama no responde (caído o mal configurado). */
	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<String> modeloNoDisponible(ResourceAccessException e) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
				.body("El modelo de IA no está disponible. Comprueba que Ollama está en marcha.");
	}
}
