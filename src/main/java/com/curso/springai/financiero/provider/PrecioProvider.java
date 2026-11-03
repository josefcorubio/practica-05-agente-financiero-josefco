package com.curso.springai.financiero.provider;

import com.curso.springai.financiero.model.Cotizacion;

/**
 * Origen de los precios. Hoy hay un simulador; se puede sustituir por una API real.
 */
public interface PrecioProvider {
	Cotizacion obtenerCotizacion(String simbolo);
}
