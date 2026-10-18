package ro.frit.library.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record LoanRequest(@NotBlank String bookId, @NotBlank @Email String memberEmail) {

  public LoanRequest {
    if (bookId == null || bookId.isBlank()) {
      throw new IllegalArgumentException("Book ID cannot be null or blank");
    }
    if (memberEmail == null || memberEmail.isBlank()) {
      throw new IllegalArgumentException("Member Email cannot be null or blank");
    }
  }
}
