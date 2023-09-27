package br.com.bridi.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import br.com.bridi.entity.Pessoa;

@Component
public class PessoaService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private List<Pessoa> listaPessoas = new ArrayList<Pessoa>();

  private static final RowMapper<Pessoa> MAPPER_PESSOA = (row, i) -> {
    Pessoa pessoa = new Pessoa();
    pessoa.setId(row.getString("id"));
    pessoa.setApelido(row.getString("apelido"));
    pessoa.setNome(row.getString("nome"));
    pessoa.setNascimento(row.getString("nascimento"));
    pessoa.setStack(List.of("a"));
    pessoa.setTermSearch("");
    return pessoa;
  };

  public String inserir(final Pessoa p) {
    p.setId(UUID.randomUUID().toString());
    listaPessoas.add(p);

    return p.getId().toString();
  }

  public Long count() {
    return jdbcTemplate.queryForObject("select count(*) from pessoas", Long.class);
  }

  public Optional<Pessoa> findById(String id) {
    return jdbcTemplate.query(
        "select id, apelido, nome, nascimento, stack from pessoas where id = ?",
        rs -> rs.next() ? Optional.ofNullable(MAPPER_PESSOA.mapRow(rs, 1)) : Optional.empty(), id);
  }
  
  public List<Pessoa> listarTodosPorTermo(String termo) {
    return jdbcTemplate.query("SELECT id, apelido, nome, nascimento, stack FROM pessoas p WHERE termo LIKE ? LIMIT 50",
            MAPPER_PESSOA,
            '%'+termo+'%');
  }

  @Scheduled(fixedDelay = 2000)
  private void batchInsert() {

    List<Pessoa> p = new ArrayList<Pessoa>(listaPessoas);
    listaPessoas.clear();

    jdbcTemplate.batchUpdate(
        "INSERT INTO PESSOAS (id, nome, apelido, nascimento, stack, termo) values (?,?,?,?,?,?)",
        new BatchPreparedStatementSetter() {

          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, p.get(i).getId());
            ps.setString(2, p.get(i).getNome());
            ps.setString(3, p.get(i).getApelido());
            ps.setString(4, p.get(i).getNascimento());
            ps.setString(5, p.get(i).getStack());
            ps.setString(6, p.get(i).getTermSearch());
          }

          @Override
          public int getBatchSize() {
            return p.size();
          }
        });

  }

}
