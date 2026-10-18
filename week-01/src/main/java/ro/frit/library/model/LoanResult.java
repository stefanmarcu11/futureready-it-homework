package ro.frit.library.model;

import ro.frit.library.service.Library;

public sealed interface LoanResult permits LoanResult.Loaned, LoanResult.NotAvailable, LoanResult.UnknownBook {

  record Loaned(Loan loan) implements LoanResult {}

  record NotAvailable(String bookId) implements LoanResult {}

  record UnknownBook(String bookId) implements LoanResult {}

  default String describe() {
    return switch (this) {
      case Loaned(Loan loan) ->
              "Loan #" + loan.getId() + " created for " + loan.getMemberEmail();
      case NotAvailable(String bookId) ->
              "No free copy available for book " + bookId;
      case UnknownBook(String bookId) ->
              "Unknown book with id " + bookId;
    };
  }
}
