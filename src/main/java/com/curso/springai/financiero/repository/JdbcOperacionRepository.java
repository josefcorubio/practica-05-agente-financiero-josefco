package com.curso.springai.financiero.repository;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.curso.springai.financiero.model.Operacion;
import com.curso.springai.financiero.model.TipoOperacion;

@Repository
public class JdbcOperacionRepository implements OperacionRepository {

	private final JdbcClient jdbc;

	public JdbcOperacionRepository(JdbcClient jdbc) {
		this.jdbc = jdbc;
	}

	@Override
	public void guardar(Operacion operacion) {
		jdbc.sql("INSERT INTO Operaciones (operacion, simbolo_accion, precio, fecha) VALUES (?, ?, ?, ?)")
				.params(operacion.operacion().name(), operacion.simboloAccion(), operacion.precio(), operacion.fecha())
				.update();
	}

	@Override
	public List<Operacion> listar() {
		return jdbc.sql("SELECT operacion, simbolo_accion, precio, fecha FROM Operaciones ORDER BY id")
				.query((rs, n) -> new Operacion(TipoOperacion.valueOf(rs.getString("operacion")),
						rs.getString("simbolo_accion"), rs.getDouble("precio"),
						rs.getDate("fecha").toLocalDate()))
				.list();
	}
}
