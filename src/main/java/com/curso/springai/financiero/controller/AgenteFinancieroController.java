package com.curso.springai.financiero.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.curso.springai.financiero.model.Operacion;
import com.curso.springai.financiero.service.AgenteFinancieroService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("agente")
public class AgenteFinancieroController {

	private final AgenteFinancieroService agenteService;

	public AgenteFinancieroController(AgenteFinancieroService agenteService) {
		this.agenteService = agenteService;
	}

	/** Paso 1: precio, estado y recomendación de la acción. Ej: /agente/analizar?accion=AAPL */
	@GetMapping("analizar")
	public ResponseEntity<String> analizar(@RequestParam String accion, HttpSession sesion) {
		return ResponseEntity.ok(agenteService.analizar(accion, sesion.getId()));
	}

	/** Paso 2: decisión del usuario sobre la acción analizada. Ej: /agente/operar?respuesta=comprar */
	@GetMapping("operar")
	public ResponseEntity<String> operar(@RequestParam String respuesta, HttpSession sesion) {
		return ResponseEntity.ok(agenteService.operar(respuesta, sesion.getId()));
	}

	/** Operaciones registradas hasta ahora (en memoria). Ej: /agente/operaciones */
	@GetMapping("operaciones")
	public ResponseEntity<List<Operacion>> operaciones() {
		return ResponseEntity.ok(agenteService.operacionesRegistradas());
	}
}
