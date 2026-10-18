package ro.frit.library.model;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
public class Book {
  private String id;
  private String title;
  private String author;
  private int copies;
  private int loanedCopies;
}
