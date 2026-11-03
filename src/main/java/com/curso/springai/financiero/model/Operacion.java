package com.curso.springai.financiero.model;

import java.time.LocalDate;

/**
 * Operación elegida por el usuario: operacion, simbolo_accion, precio, fecha.
 */
public record Operacion(TipoOperacion operacion, String simboloAccion, double precio, LocalDate fecha) {
}
