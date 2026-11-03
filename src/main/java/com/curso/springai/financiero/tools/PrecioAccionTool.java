package com.curso.springai.financiero.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.curso.springai.financiero.model.Cotizacion;
import com.curso.springai.financiero.provider.PrecioProvider;

@Component
public class PrecioAccionTool {

	private final PrecioProvider precioProvider;

	public PrecioAccionTool(PrecioProvider precioProvider) {
		this.precioProvider = precioProvider;
	}

	@Tool(name = "precioAccion", description = "Devuelve el precio actual de una acción y sus últimos cierres diarios (del más antiguo al más reciente)")
	public Cotizacion precioAccion(@ToolParam(description = "símbolo de la acción, por ejemplo AAPL") String simbolo) {
		return precioProvider.obtenerCotizacion(simbolo);
	}
}
