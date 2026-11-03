package com.curso.springai.financiero.repository;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Repository;

import com.curso.springai.financiero.model.Operacion;

@Repository
public class InMemoryOperacionRepository implements OperacionRepository {

	private final List<Operacion> operaciones = new CopyOnWriteArrayList<>();

	@Override
	public void guardar(Operacion operacion) {
		operaciones.add(operacion);
	}

	@Override
	public List<Operacion> listar() {
		return List.copyOf(operaciones);
	}
}
