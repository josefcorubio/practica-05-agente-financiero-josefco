// package com.curso.springai.financiero.tools;
// 
// import java.util.List;
// 
// import org.springframework.ai.tool.annotation.Tool;
// import org.springframework.ai.tool.annotation.ToolParam;
// import org.springframework.stereotype.Component;
// 
// import com.curso.springai.financiero.model.AnalisisAccion;
// import com.curso.springai.financiero.model.Cotizacion;
// import com.curso.springai.financiero.model.Tendencia;
// import com.curso.springai.financiero.provider.PrecioProvider;
// 
// @Component
// public class TendenciaTool {
// 
// 	private final PrecioProvider precioProvider;
// 
// 	public TendenciaTool(PrecioProvider precioProvider) {
// 		this.precioProvider = precioProvider;
// 	}
// 
// 	@Tool(name = "tendenciaAccion", description = "Determina si una acción está SUBIENDO o BAJANDO, comparando su precio actual con el de hace unos días. Devuelve también el precio actual y la variación porcentual")
// 	public AnalisisAccion tendenciaAccion(@ToolParam(description = "símbolo de la acción, por ejemplo AAPL") String simbolo) {
// 		Cotizacion cotizacion = precioProvider.obtenerCotizacion(simbolo);
// 		List<Double> historico = cotizacion.historico();
// 		double inicial = historico.get(0);
// 		double variacion = (cotizacion.precioActual() - inicial) / inicial * 100;
// 		Tendencia tendencia = variacion >= 0 ? Tendencia.SUBIENDO : Tendencia.BAJANDO;
// 		return new AnalisisAccion(cotizacion.simbolo(), cotizacion.precioActual(), tendencia,
// 				Math.round(variacion * 100.0) / 100.0);
// 	}
// }
