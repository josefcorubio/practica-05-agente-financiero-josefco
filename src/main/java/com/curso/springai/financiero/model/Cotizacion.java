package com.curso.springai.financiero.model;

import java.util.List;

/**
 * Precio actual de una acción y sus últimos cierres (del más antiguo al más reciente).
 */
public record Cotizacion(String simbolo, double precioActual, List<Double> historico) {
}
