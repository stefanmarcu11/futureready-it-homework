package ro.frit.library.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class Loan {
  private String id;
  private String bookId;
  private String memberEmail;
  private LocalDateTime loanedAt;
}
