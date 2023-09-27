package br.com.bridi.entity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Pessoa {

  private static final String SPLIT_CHAR = ";";
  
  @NotNull
  private String id;
  private String nome;
  private String apelido;
  private String nascimento;
  private List<String> stack;
  private String termSearch;
  
  public boolean isValid() {

    boolean validName = !this.nome.isEmpty() && this.nome.length() < 33;
    boolean validNickname = !this.apelido.isEmpty() && this.apelido.length() < 101;
    boolean validDate = this.validateDate();
    boolean validStack = this.validateStack();

    return validName && validNickname && validDate && validStack;
  }

  private boolean validateDate() {
    try {
      LocalDate.parse(getNascimento());
      
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String getStack() {
    return String.join(SPLIT_CHAR, stack);
  }
  
  private boolean validateStack() {
    if (CollectionUtils.isEmpty(stack)) {
      return true;
    }

    return stack.stream().anyMatch(s -> s.length() < 33);
  }


  @Override
  public String toString() {
    return "[nome=" + nome + ", apelido=" + apelido + ", nascimento=" + nascimento
        + ", stack=" + getStack() + "]";
  }

  

}
