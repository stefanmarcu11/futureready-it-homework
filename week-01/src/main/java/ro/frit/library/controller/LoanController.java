package ro.frit.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.frit.library.model.Loan;
import ro.frit.library.model.LoanRequest;
import ro.frit.library.model.LoanResult;
import ro.frit.library.service.Library;

@RestController
@RequestMapping("/books/{id}/loans")
@RequiredArgsConstructor
public class LoanController {

  public record LoanBody(String memberEmail) {}

  private final Library library;

  @PostMapping
  public ResponseEntity<?> lend(@PathVariable String id, @RequestBody LoanBody body) {
    LoanResult result = library.lend(new LoanRequest(id, body.memberEmail()));
    return switch (result) {
      case LoanResult.Loaned(Loan loan) ->
              ResponseEntity.status(HttpStatus.CREATED).body(loan);
      case LoanResult.UnknownBook(String bookId) ->
              ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                      ProblemDetail.forStatusAndDetail(
                              HttpStatus.NOT_FOUND, "Unknown book with id: " + bookId));
      case LoanResult.NotAvailable(String bookId) ->
              ResponseEntity.status(HttpStatus.CONFLICT).body(
                      ProblemDetail.forStatusAndDetail(
                              HttpStatus.CONFLICT, "No free copy available for book id: " + bookId));
    };
  }

}
