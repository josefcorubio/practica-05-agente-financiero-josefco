package com.curso.springai.financiero.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import com.curso.springai.financiero.tools.PrecioAccionTool;
import com.curso.springai.financiero.tools.RegistrarOperacionTool;
import com.curso.springai.financiero.tools.TendenciaTool;

@Service
public class AgenteFinancieroService {

	private static final String SYSTEM = """
			Eres un agente financiero. Respondes siempre en español.

			ANÁLISIS DE UNA ACCIÓN
			Cuando el usuario te indique una acción (su símbolo):
			1. Utiliza SIEMPRE la herramienta tendenciaAccion para obtener el precio actual
			   y saber si la acción está SUBIENDO o BAJANDO. No inventes precios.
			2. Elabora una recomendación: COMPRAR, VENDER o MANTENER.
			3. Responde con este formato:
			   - Acción: <símbolo>
			   - Precio actual: <precio>
			   - Estado: <subiendo | bajando>
			   - Recomendación: <comprar | vender | mantener> (con una justificación breve)
			4. Termina preguntando al usuario si quiere comprar, vender o mantener.

			REGISTRO DE LA OPERACIÓN
			Cuando el usuario responda con la operación que desea (comprar, vender o mantener),
			utiliza la herramienta registrarOperacion con esa operación y el símbolo de la acción
			analizada anteriormente en la conversación.
			Después confirma al usuario la operación registrada (operación, acción, precio y fecha).
			Si el usuario no deja claro qué operación quiere, vuelve a preguntárselo sin registrar nada.
			""";

	private final ChatClient chatClient;
	private final PrecioAccionTool precioAccionTool;
	private final TendenciaTool tendenciaTool;
	private final RegistrarOperacionTool registrarOperacionTool;

	public AgenteFinancieroService(ChatClient chatClient, PrecioAccionTool precioAccionTool,
			TendenciaTool tendenciaTool, RegistrarOperacionTool registrarOperacionTool) {
		this.chatClient = chatClient;
		this.precioAccionTool = precioAccionTool;
		this.tendenciaTool = tendenciaTool;
		this.registrarOperacionTool = registrarOperacionTool;
	}

	public String analizar(String accion, String conversationId) {
		return preguntar("Analiza la acción " + accion, conversationId);
	}

	public String operar(String respuestaUsuario, String conversationId) {
		return preguntar("Respuesta del usuario: " + respuestaUsuario, conversationId);
	}

	private String preguntar(String mensajeUsuario, String conversationId) {
		return chatClient.prompt()
				.system(SYSTEM)
				.user(mensajeUsuario)
				.tools(precioAccionTool, tendenciaTool, registrarOperacionTool)
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
				.call()
				.content();
	}
}
