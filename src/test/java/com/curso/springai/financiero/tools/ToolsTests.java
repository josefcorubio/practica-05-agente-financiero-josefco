package com.curso.springai.financiero.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.curso.springai.financiero.model.AnalisisAccion;
import com.curso.springai.financiero.model.Cotizacion;
import com.curso.springai.financiero.model.Operacion;
import com.curso.springai.financiero.model.Tendencia;
import com.curso.springai.financiero.model.TipoOperacion;
import com.curso.springai.financiero.provider.SimuladorPrecioProvider;
import com.curso.springai.financiero.repository.InMemoryOperacionRepository;

class ToolsTests {

	private final SimuladorPrecioProvider provider = new SimuladorPrecioProvider();

	@Test
	void simuladorEsDeterministaEIgnoraMayusculas() {
		assertEquals(provider.obtenerCotizacion("AAPL"), provider.obtenerCotizacion(" aapl "));
	}

	@Test
	void precioAccionDevuelveCotizacionConHistorico() {
		Cotizacion cotizacion = new PrecioAccionTool(provider).precioAccion("MSFT");
		assertEquals("MSFT", cotizacion.simbolo());
		assertEquals(6, cotizacion.historico().size());
		assertEquals(cotizacion.precioActual(), cotizacion.historico().get(5));
	}

	@Test
	void tendenciaCoincideConElHistorico() {
		for (String simbolo : new String[] { "AAPL", "TSLA", "MSFT", "GOOG" }) {
			AnalisisAccion analisis = new TendenciaTool(provider).tendenciaAccion(simbolo);
			Cotizacion c = provider.obtenerCotizacion(simbolo);
			Tendencia esperada = c.precioActual() >= c.historico().get(0) ? Tendencia.SUBIENDO : Tendencia.BAJANDO;
			assertEquals(esperada, analisis.tendencia(), simbolo);
			assertEquals(c.precioActual(), analisis.precioActual(), simbolo);
		}
	}

	@Test
	void registrarOperacionGuardaPrecioYFechaDelSistema() {
		InMemoryOperacionRepository repo = new InMemoryOperacionRepository();
		Operacion op = new RegistrarOperacionTool(repo, provider).registrarOperacion(TipoOperacion.COMPRAR, "msft");
		assertEquals(TipoOperacion.COMPRAR, op.operacion());
		assertEquals("MSFT", op.simboloAccion());
		assertEquals(provider.obtenerCotizacion("MSFT").precioActual(), op.precio());
		assertNotNull(op.fecha());
		assertEquals(1, repo.listar().size());
	}
}
