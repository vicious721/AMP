package br.edu.ifsp.spo.dados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.ifsp.spo.model.Exercicio;
import br.edu.ifsp.spo.model.Prova;

public class ExercicioDAO implements ExercicioDAOInterface {

    Connection conexao;

    @Override
    public void inserirExercicio(Exercicio exercicio) {
        if (!VerificarDAO.validarExercicio(exercicio)) {
            try {
                conexao = Conexao.abrir();
                String sql = "INSERT INTO Exercicio VALUES(DEFAULT, ?, ?, ?)";
                PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, exercicio.getNomeExercicio());
                ps.setString(2, exercicio.getNomeArquivo());
                ps.setInt(3, exercicio.getProva().getIdProva());
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()){
                	exercicio.setIdExercicio(rs.getInt(1));
                }

                Conexao.fechar(conexao);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void atualizarExercicio(Exercicio exercicio) {
        if (VerificarDAO.validarExercicio(exercicio)) {
            try {
                conexao = Conexao.abrir();
                String sql = "UPDATE Exercicio SET Nome_Exercicio = ?, Nome_Arquivo = ?, Codigo_Prova = ? WHERE Codigo_Exercicio = ?";
                PreparedStatement ps = conexao.prepareStatement(sql);
                ps.setString(1, exercicio.getNomeExercicio());
                ps.setString(2, exercicio.getNomeArquivo());
                ps.setInt(3, exercicio.getProva().getIdProva());
                ps.setInt(4, exercicio.getIdExercicio());
                ps.executeUpdate();
                Conexao.fechar(conexao);
            } catch (SQLException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    @Override
    public void remover(Exercicio exercicio) {
        if (VerificarDAO.validarExercicio(exercicio)) {
            conexao = Conexao.abrir();
            String sql = "DELETE FROM Exercicio WHERE Codigo_Exercicio = ?";
            try {
                PreparedStatement ps = conexao.prepareStatement(sql);
                ps.setInt(1, exercicio.getIdExercicio());
                ps.executeUpdate();
                Conexao.fechar(conexao);
            } catch (SQLException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
    }

    @Override
    public List<Exercicio> pesquisarPorCodigo(int codigo) {
        List<Exercicio> exerciciosEncontrados = new ArrayList<>();
        try {
            conexao = Conexao.abrir();
            String sql = "SELECT * FROM Exercicio "
                    + "INNER JOIN Prova ON Exercicio.Codigo_Prova = Prova.Codigo_Prova"
                    + " WHERE Exercicio.Codigo_Exercicio LIKE ?";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setString(1, "%" + codigo + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                exerciciosEncontrados.add(new Exercicio(rs.getInt(1), rs.getString(2), rs.getString(3),
                		new Prova(rs.getInt(4), null, null)));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        Conexao.fechar(conexao);
        return exerciciosEncontrados;

    }

    @Override
    public List<Exercicio> pesquisarExercicios() {
        List<Exercicio> exerciciosEncontrados = new ArrayList<>();
        try {
            conexao = Conexao.abrir();
            String sql = "SELECT * FROM Exercicio "
                    + "INNER JOIN Prova ON Exercicio.Codigo_Prova = Prova.Codigo_Prova";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
            	exerciciosEncontrados.add(new Exercicio(rs.getInt(1), rs.getString(2), rs.getString(3),
                		new Prova(rs.getInt(4), null, null)));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }

        Conexao.fechar(conexao);
        return exerciciosEncontrados;
    }
}
