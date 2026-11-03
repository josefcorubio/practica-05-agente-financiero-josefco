package com.curso.springai.financiero.repository;

import java.util.List;

import com.curso.springai.financiero.model.Operacion;

/**
 * Registro de operaciones. De momento en memoria; se sustituirá por MySQL en un hito posterior.
 */
public interface OperacionRepository {
	void guardar(Operacion operacion);

	List<Operacion> listar();
}
