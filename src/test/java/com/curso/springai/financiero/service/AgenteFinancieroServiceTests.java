package com.curso.springai.financiero.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.curso.springai.financiero.model.TipoOperacion;
import com.curso.springai.financiero.provider.SimuladorPrecioProvider;
import com.curso.springai.financiero.repository.InMemoryOperacionRepository;
import com.curso.springai.financiero.tools.PrecioAccionTool;
import com.curso.springai.financiero.tools.RegistrarOperacionTool;
import com.curso.springai.financiero.tools.TendenciaTool;

/** Casos que no llegan al LLM, por lo que el ChatClient puede ser null. */
class AgenteFinancieroServiceTests {

	private final SimuladorPrecioProvider provider = new SimuladorPrecioProvider();
	private final InMemoryOperacionRepository repo = new InMemoryOperacionRepository();
	private final AgenteFinancieroService service = new AgenteFinancieroService(null,
			new PrecioAccionTool(provider), new TendenciaTool(provider),
			new RegistrarOperacionTool(repo, provider), repo);

	@Test
	void interpretaRespuestasValidas() {
		assertEquals(TipoOperacion.COMPRAR, AgenteFinancieroService.interpretar("Comprar"));
		assertEquals(TipoOperacion.COMPRAR, AgenteFinancieroService.interpretar("Quiero COMPRAR"));
		assertEquals(TipoOperacion.VENDER, AgenteFinancieroService.interpretar("vendo"));
		assertEquals(TipoOperacion.MANTENER, AgenteFinancieroService.interpretar("mantener"));
	}

	@Test
	void rechazaRespuestasAmbiguasOVacias() {
		assertNull(AgenteFinancieroService.interpretar("no sé"));
		assertNull(AgenteFinancieroService.interpretar("comprar y vender"));
		assertNull(AgenteFinancieroService.interpretar(""));
		assertNull(AgenteFinancieroService.interpretar(null));
	}

	@Test
	void noRegistraSiNoSeHaAnalizadoNingunaAccion() {
		String respuesta = service.operar("comprar", "sesion-1");
		assertTrue(respuesta.startsWith("Primero indica la acción"));
		assertTrue(repo.listar().isEmpty());
	}

	@Test
	void rechazaSimboloVacioOInvalidoSinLlamarAlModelo() {
		assertTrue(service.analizar(" ", "sesion-1").startsWith("Indica el símbolo"));
		assertTrue(service.analizar("AAPL; DROP", "sesion-1").contains("no es válido"));
		assertTrue(service.analizar("123", "sesion-1").contains("no es válido"));
	}
}
