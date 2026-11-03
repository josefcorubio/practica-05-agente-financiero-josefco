package com.curso.springai.financiero.provider;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.curso.springai.financiero.model.Cotizacion;

/**
 * Simulador determinista: para un mismo símbolo y semana devuelve siempre la misma serie,
 * con una tendencia (al alza o a la baja) que depende del símbolo.
 */
@Component
public class SimuladorPrecioProvider implements PrecioProvider {

	private static final int DIAS_HISTORICO = 6;

	@Override
	public Cotizacion obtenerCotizacion(String simbolo) {
		String clave = simbolo.trim().toUpperCase(Locale.ROOT);
		long semana = ChronoUnit.WEEKS.between(LocalDate.EPOCH, LocalDate.now());
		Random random = new Random(clave.hashCode() * 31L + semana);

		double precio = 20 + random.nextDouble() * 480;
		double deriva = (random.nextDouble() - 0.5) * 0.06;

		List<Double> historico = new ArrayList<>();
		for (int i = 0; i < DIAS_HISTORICO; i++) {
			historico.add(redondear(precio));
			precio = precio * (1 + deriva + (random.nextDouble() - 0.5) * 0.01);
		}
		return new Cotizacion(clave, historico.get(historico.size() - 1), historico);
	}

	private double redondear(double valor) {
		return Math.round(valor * 100.0) / 100.0;
	}
}
