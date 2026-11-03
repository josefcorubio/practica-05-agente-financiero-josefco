# Práctica 5: Agente financiero

## Enunciado

En este proyecto se trata de desarrollar un agente financiero. La aplicación consistirá en un microservicio que recibirá como parámetro el símbolo de la acción. El agente comprobará el precio de dicha acción y determinará si está subiendo o bajando; en función de eso, elaborará una recomendación para el usuario que incluirá precio actual de la acción, estado (subiendo o bajando) y recomendación (comprar, vender o mantener). Además, se le preguntará al usuario si quiere comprar, vender o mantener, y la operación elegida se registrará en una base de datos (operación, símbolo de acción, precio, fecha).


## Estado actual (versión no persistente)

Implementado con Spring Boot + Spring AI + Ollama. Las operaciones se guardan **en memoria** y se pierden al reiniciar.

- **Precios:** simulados (`SimuladorPrecioProvider`). Para un mismo símbolo y semana devuelve siempre la misma serie de 6 cierres diarios.
- **Tools del agente:** `precioAccion`, `tendenciaAccion` (SUBIENDO / BAJANDO) y `registrarOperacion`.
- **Memoria de conversación:** en RAM, identificada por la sesión HTTP (cookie `JSESSIONID`).
- **Registro:** `OperacionRepository` (interfaz) con implementación en memoria (`operacion`, `simbolo_accion`, `precio`, `fecha`).

## Requisitos

- Java 21
- llama3.2
- La aplicación arranca en el puerto 8005.

## Uso

El flujo tiene dos pasos y **debe compartir la sesión** entre ambas llamadas.

1. Analizar una acción (precio, estado y recomendación):

```
curl -c cookies.txt "http://localhost:8005/agente/analizar?accion=AAPL"
```

2. Indicar la operación elegida (comprar, vender o mantener):

```
curl -b cookies.txt "http://localhost:8005/agente/operar?respuesta=comprar"
```

3. Consultar las operaciones registradas (en memoria):

```
curl "http://localhost:8005/agente/operaciones"
```

| Endpoint | Parámetro | Descripción |
|---|---|---|
| `GET /agente/analizar` | `accion` | Precio actual, estado y recomendación; pregunta qué operación quiere el usuario |
| `GET /agente/operar` | `respuesta` | Registra la operación elegida sobre la última acción analizada |
| `GET /agente/operaciones` | — | Lista las operaciones registradas |

