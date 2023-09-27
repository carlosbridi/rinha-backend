package br.com.bridi.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.com.bridi.entity.Pessoa;
import br.com.bridi.service.PessoaService;

@RestController(value = "/")
@RequestMapping
public class PessoaController {

  @Autowired
  PessoaService pService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> insertPessoa(@RequestBody Pessoa pessoa) {
  
    if (pessoa.isValid()) {
      pessoa.setTermSearch(pessoa.toString());
      String inserir = pService.inserir(pessoa);
      return ResponseEntity.created(URI.create("/"+inserir)).build();
    } 
    
    return ResponseEntity.unprocessableEntity().build();
  }
  
  @GetMapping(path = "/pessoas/{id}")
  public ResponseEntity<Pessoa> getById(@PathVariable(value = "id") String id){
    Optional<Pessoa> findById = pService.findById(id);
    if (findById.isPresent())
      return ResponseEntity.ok(findById.get());
    else
      return ResponseEntity.notFound().build();
  }

  
  @GetMapping
  public ResponseEntity<List<Pessoa>> findById(@RequestParam(value = "t") String t) {
    if (t == null || t.isEmpty() || t.isBlank()) {
        return ResponseEntity.badRequest().build();
    }else {    
      List<Pessoa> listarTodosPorTermo = pService.listarTodosPorTermo(t);
      return ResponseEntity.ok(listarTodosPorTermo);      
    }
  }

  @GetMapping("/contagem-pessoas")
  public ResponseEntity<Long> contagemPessoas() {
      return ResponseEntity.ok(pService.count());
  }    

}
