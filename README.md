# Práctica 5: Agente financiero

## Enunciado

En este proyecto se trata de desarrollar un agente financiero. La aplicación consistirá en un microservicio que recibirá como parámetro el símbolo de la acción. El agente comprobará el precio de dicha acción y determinará si está subiendo o bajando; en función de eso, elaborará una recomendación para el usuario que incluirá precio actual de la acción, estado (subiendo o bajando) y recomendación (comprar, vender o mantener). Además, se le preguntará al usuario si quiere comprar, vender o mantener, y la operación elegida se registrará en una base de datos (operación, símbolo de acción, precio, fecha).


## Estado actual

Implementado con Spring Boot + Spring AI + Ollama + MySQL. Las operaciones se guardan de forma **persistente** en la tabla `Operaciones`.

- **Precios:** simulados (`SimuladorPrecioProvider`). Para un mismo símbolo y semana devuelve siempre la misma serie de 6 cierres diarios.
- **Tools:** `precioAccion` y `tendenciaAccion` (SUBIENDO / BAJANDO) las usa el LLM; `registrarOperacion` la invoca el servicio tras validar la respuesta del usuario.
- **Memoria de conversación:** en RAM, identificada por la sesión HTTP (cookie `JSESSIONID`).
- **Registro:** `OperacionRepository` (interfaz) con implementación JDBC (`JdbcOperacionRepository`) sobre MySQL: `operacion`, `simbolo_accion`, `precio`, `fecha`.

## Requisitos

- Java 21
- Ollama con el modelo llama3.2
- MySQL en `localhost:3307` con la base de datos `Finanzas_Data` (usuario `root`, sin contraseña; configurable en `application.properties`)
- La aplicación arranca en el puerto 8005.

Tabla:

```sql
CREATE DATABASE Finanzas_Data CHARACTER SET utf8mb4;
CREATE TABLE Finanzas_Data.Operaciones (
  id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  operacion      ENUM('COMPRAR','VENDER','MANTENER') NOT NULL,
  simbolo_accion VARCHAR(10)   NOT NULL,
  precio         DECIMAL(12,2) NOT NULL,
  fecha          DATE          NOT NULL,
  PRIMARY KEY (id),
  KEY idx_operaciones_simbolo (simbolo_accion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

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

3. Consultar las operaciones registradas (leídas de MySQL):

```
curl "http://localhost:8005/agente/operaciones"
```

| Endpoint | Parámetro | Descripción |
|---|---|---|
| `GET /agente/analizar` | `accion` | Precio actual, estado y recomendación; pregunta qué operación quiere el usuario |
| `GET /agente/operar` | `respuesta` | Registra la operación elegida sobre la última acción analizada |
| `GET /agente/operaciones` | — | Lista las operaciones registradas |

