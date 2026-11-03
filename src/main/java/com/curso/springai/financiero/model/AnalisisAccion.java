package com.curso.springai.financiero.model;

public record AnalisisAccion(String simbolo, double precioActual, Tendencia tendencia, double variacionPorcentual) {
}
