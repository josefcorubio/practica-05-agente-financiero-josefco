package com.curso.springai.financiero.service;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import com.curso.springai.financiero.model.Operacion;
import com.curso.springai.financiero.model.TipoOperacion;
import com.curso.springai.financiero.repository.OperacionRepository;
import com.curso.springai.financiero.tools.PrecioAccionTool;
import com.curso.springai.financiero.tools.RegistrarOperacionTool;
import com.curso.springai.financiero.tools.TendenciaTool;

@Service
public class AgenteFinancieroService {

	private static final String SYSTEM = """
			Eres un agente financiero. Respondes siempre en español.

			Cuando el usuario te indique una acción (su símbolo):
			1. Utiliza SIEMPRE la herramienta tendenciaAccion para obtener el precio actual
			   y saber si la acción está SUBIENDO o BAJANDO. No inventes precios.
			2. Elabora una recomendación: COMPRAR si está SUBIENDO, VENDER si está BAJANDO,
			   o MANTENER si la variación es muy pequeña.
			3. Responde con este formato:
			   - Acción: <símbolo>
			   - Precio actual: <precio>
			   - Estado: <subiendo | bajando>
			   - Recomendación: <comprar | vender | mantener> (justificación de una frase)
			4. En la justificación usa SOLO los datos devueltos por la herramienta (precio, estado
			   y variación porcentual). No menciones periodos de tiempo (días, semanas, meses),
			   ni precios de compra, ni noticias, ni ningún dato que la herramienta no haya devuelto.
			5. Termina SIEMPRE con la pregunta: "¿Quiere comprar, vender o mantener?"
			""";

	private final Map<String, String> ultimaAccionAnalizada = new ConcurrentHashMap<>();

	private final ChatClient chatClient;
	private final PrecioAccionTool precioAccionTool;
	private final TendenciaTool tendenciaTool;
	private final RegistrarOperacionTool registrarOperacionTool;
	private final OperacionRepository operacionRepository;

	public AgenteFinancieroService(ChatClient chatClient, PrecioAccionTool precioAccionTool,
			TendenciaTool tendenciaTool, RegistrarOperacionTool registrarOperacionTool,
			OperacionRepository operacionRepository) {
		this.chatClient = chatClient;
		this.precioAccionTool = precioAccionTool;
		this.tendenciaTool = tendenciaTool;
		this.registrarOperacionTool = registrarOperacionTool;
		this.operacionRepository = operacionRepository;
	}

	public String analizar(String accion, String conversationId) {
		if (accion == null || accion.isBlank()) {
			return "Indica el símbolo de la acción que quieres analizar, por ejemplo AAPL.";
		}
		String simbolo = accion.trim().toUpperCase();
		String respuesta = preguntar("Analiza la acción " + simbolo, conversationId);
		ultimaAccionAnalizada.put(conversationId, simbolo);
		return respuesta;
	}

	/**
	 * Registra la operación elegida. La decisión de registrar no se delega en el LLM
	 * (un modelo pequeño inventaba símbolos o pedía confirmaciones tardías): se valida
	 * aquí la respuesta y se invoca directamente la herramienta de registro.
	 */
	public String operar(String respuestaUsuario, String conversationId) {
		String simbolo = ultimaAccionAnalizada.get(conversationId);
		if (simbolo == null) {
			return "Primero indica la acción que quieres analizar (/agente/analizar?accion=AAPL).";
		}
		TipoOperacion tipo = interpretar(respuestaUsuario);
		if (tipo == null) {
			return "No he entendido tu respuesta. Elige una opción: comprar, vender o mantener.";
		}
		Operacion operacion = registrarOperacionTool.registrarOperacion(tipo, simbolo);
		ultimaAccionAnalizada.remove(conversationId);
		return """
				Operación registrada:
				- Operación: %s
				- Acción: %s
				- Precio: %s
				- Fecha: %s""".formatted(operacion.operacion().name().toLowerCase(), operacion.simboloAccion(),
				operacion.precio(), operacion.fecha());
	}

	/** Devuelve la operación elegida, o null si la respuesta no es una única opción clara. */
	static TipoOperacion interpretar(String respuesta) {
		if (respuesta == null) {
			return null;
		}
		String texto = Normalizer.normalize(respuesta.toLowerCase(), Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "");
		boolean comprar = texto.contains("compr");
		boolean vender = texto.contains("vend") || texto.contains("venta");
		boolean mantener = texto.contains("mant");
		if (comprar && !vender && !mantener) {
			return TipoOperacion.COMPRAR;
		}
		if (vender && !comprar && !mantener) {
			return TipoOperacion.VENDER;
		}
		if (mantener && !comprar && !vender) {
			return TipoOperacion.MANTENER;
		}
		return null;
	}

	public List<Operacion> operacionesRegistradas() {
		return operacionRepository.listar();
	}

	private String preguntar(String mensajeUsuario, String conversationId) {
		return chatClient.prompt()
				.system(SYSTEM)
				.user(mensajeUsuario)
				.tools(precioAccionTool, tendenciaTool)
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
				.call()
				.content();
	}
}
