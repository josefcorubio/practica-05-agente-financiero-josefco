// package com.curso.springai.financiero.tools;
// 
// import java.time.LocalDate;
// 
// import org.springframework.ai.tool.annotation.Tool;
// import org.springframework.ai.tool.annotation.ToolParam;
// import org.springframework.stereotype.Component;
// 
// import com.curso.springai.financiero.model.Operacion;
// import com.curso.springai.financiero.model.TipoOperacion;
// import com.curso.springai.financiero.provider.PrecioProvider;
// import com.curso.springai.financiero.repository.OperacionRepository;
// 
// @Component
// public class RegistrarOperacionTool {
// 
// 	private final OperacionRepository operacionRepository;
// 	private final PrecioProvider precioProvider;
// 
// 	public RegistrarOperacionTool(OperacionRepository operacionRepository, PrecioProvider precioProvider) {
// 		this.operacionRepository = operacionRepository;
// 		this.precioProvider = precioProvider;
// 	}
// 
// 	@Tool(name = "registrarOperacion", description = "Registra la operación elegida por el usuario (COMPRAR, VENDER o MANTENER) sobre una acción. El precio y la fecha se obtienen automáticamente")
// 	public Operacion registrarOperacion(
// 			@ToolParam(description = "operación elegida: COMPRAR, VENDER o MANTENER") TipoOperacion operacion,
// 			@ToolParam(description = "símbolo de la acción, por ejemplo AAPL") String simbolo) {
// 		double precio = precioProvider.obtenerCotizacion(simbolo).precioActual();
// 		Operacion registro = new Operacion(operacion, simbolo.trim().toUpperCase(), precio, LocalDate.now());
// 		operacionRepository.guardar(registro);
// 		return registro;
// 	}
// }
