package ro.frit.library.service;

import ro.frit.library.model.Book;
import ro.frit.library.model.Loan;
import ro.frit.library.model.LoanRequest;
import ro.frit.library.model.LoanResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Library {

  private final Map<String, Book> books = new LinkedHashMap<>();
  private final List<Loan> loans = new ArrayList<>();
  private final AtomicLong nextLoanId = new AtomicLong(1);

  public Library(List<Book> initialBooks) {
    initialBooks.forEach(book -> books.put(book.getId(), book));
  }

  public LoanResult lend(LoanRequest request) {
    Book book = books.get(request.bookId());

    if (book == null) {
      return new LoanResult.UnknownBook(request.bookId());
    }

    if (book.getLoanedCopies() >= book.getCopies()) {
      return new LoanResult.NotAvailable(request.bookId());
    }

    Loan loan = Loan.builder()
            .id(UUID.randomUUID().toString())
            .bookId(book.getId())
            .memberEmail(request.memberEmail())
            .loanedAt(LocalDateTime.now())
            .build();
    loans.add(loan);

    books.put(book.getId(),
            book.toBuilder().loanedCopies(book.getLoanedCopies() + 1).build());

    return new LoanResult.Loaned(loan);
  }

  public List<Book> allBooks() {
    return List.copyOf(books.values());
  }

  public Optional<Book> findBook(String id) {
    return Optional.ofNullable(books.get(id));
  }

  public List<Book> availableBooks() {
    return books.values().stream()
            .filter(book -> book.getLoanedCopies() < book.getCopies())
            .toList();
  }

  public Map<String, Long> loansPerMember() {
    return loans.stream()
            .collect(Collectors.groupingBy(Loan::getMemberEmail, Collectors.counting()));
  }

  public static String describe(LoanResult result) {
    return switch (result) {
      case LoanResult.Loaned(Loan loan) ->
              String.format("Loan created (loan id: %s, member: %s, book id: %s)", loan.getId(), loan.getMemberEmail(), loan.getBookId());
      case LoanResult.NotAvailable(String bookId) ->
              String.format("No free copy for book: %s", bookId);
      case LoanResult.UnknownBook(String bookId) ->
              String.format("Unknown book with id %s", bookId);
    };
  }
}
