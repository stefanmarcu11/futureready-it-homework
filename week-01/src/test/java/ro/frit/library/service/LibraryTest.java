package ro.frit.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ro.frit.library.model.Book;
import ro.frit.library.model.LoanRequest;
import ro.frit.library.model.LoanResult;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

  private static final String ANA = "ana@mail.ro";
  private static final String DAN = "dan@mail.ro";

  private Library library;

  @BeforeEach
  void setUp() {
    library = new Library(List.of(book("dune", 3), book("hobbit", 1)));
  }

  private static Book book(String id, int copies) {
    return Book.builder()
            .id(id).title(id).author("n/a")
            .copies(copies).loanedCopies(0)
            .build();
  }

  private LoanResult lend(String bookId, String email) {
    return library.lend(new LoanRequest(bookId, email));
  }

  private int loanedCopies(String bookId) {
    return library.findBook(bookId).orElseThrow().getLoanedCopies();
  }

  // 1
  @Test
  void lend_freeCopy_isLoanedAndIncrementsLoanedCopies() {
    LoanResult result = lend("dune", ANA);

    assertInstanceOf(LoanResult.Loaned.class, result);
    assertEquals(1, loanedCopies("dune"));
  }

  // 2: boundary, the last free copy can be borrowed
  @Test
  void lend_lastFreeCopy_isLoaned() {
    LoanResult result = lend("hobbit", ANA);

    assertInstanceOf(LoanResult.Loaned.class, result);
    assertEquals(1, loanedCopies("hobbit"));
  }

  // 3: boundary, one more than the free copies
  @Test
  void lend_noFreeCopy_isNotAvailableAndKeepsLoanedCopies() {
    lend("hobbit", ANA);

    LoanResult result = lend("hobbit", DAN);

    assertEquals(new LoanResult.NotAvailable("hobbit"), result);
    assertEquals(1, loanedCopies("hobbit"));
  }

  // 2 + 3 on a book with several copies: exactly 3 loans work, the 4th fails
  @Test
  void lend_allCopiesOfDune_thenOneMoreIsNotAvailable() {
    assertInstanceOf(LoanResult.Loaned.class, lend("dune", ANA));
    assertInstanceOf(LoanResult.Loaned.class, lend("dune", DAN));
    assertInstanceOf(LoanResult.Loaned.class, lend("dune", "eva@mail.ro"));
    assertEquals(3, loanedCopies("dune"));

    assertEquals(new LoanResult.NotAvailable("dune"), lend("dune", "ion@mail.ro"));
    assertEquals(3, loanedCopies("dune"));
  }

  // 4
  @Test
  void lend_unknownBook_isUnknownBookAndStoresNoLoan() {
    LoanResult result = lend("nope", ANA);

    assertEquals(new LoanResult.UnknownBook("nope"), result);
    assertTrue(library.loansPerMember().isEmpty());
  }

  // 5
  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   "})
  void loanRequest_blankBookId_throws(String blank) {
    assertThrows(IllegalArgumentException.class, () -> new LoanRequest(blank, ANA));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   "})
  void loanRequest_blankMemberEmail_throws(String blank) {
    assertThrows(IllegalArgumentException.class, () -> new LoanRequest("dune", blank));
  }

  // 6
  @Test
  void availableBooks_excludesBooksWithNoFreeCopy() {
    lend("dune", ANA);
    lend("hobbit", ANA);

    List<String> ids = library.availableBooks().stream().map(Book::getId).toList();

    assertEquals(List.of("dune"), ids);
  }

  // 7
  @Test
  void loansPerMember_countsLoansByEmail() {
    lend("dune", ANA);
    lend("hobbit", ANA);
    lend("dune", DAN);

    assertEquals(Map.of(ANA, 2L, DAN, 1L), library.loansPerMember());
  }

  // 8
  @Test
  void loansPerMember_withoutLoans_isEmpty() {
    assertTrue(library.loansPerMember().isEmpty());
  }

  // 9
  @Test
  void findBook_returnsBookWhenKnownAndEmptyWhenUnknown() {
    Optional<Book> dune = library.findBook("dune");

    assertTrue(dune.isPresent());
    assertEquals("dune", dune.get().getId());
    assertEquals(Optional.empty(), library.findBook("nope"));
  }

  // 10
  @Test
  void describe_namesTheBookInEveryCase() {
    LoanResult loaned = lend("dune", ANA);
    LoanResult unavailable = new LoanResult.NotAvailable("hobbit");
    LoanResult unknown = new LoanResult.UnknownBook("nope");

    assertTrue(Library.describe(loaned).contains("dune"));
    assertTrue(Library.describe(unavailable).contains("hobbit"));
    assertTrue(Library.describe(unknown).contains("nope"));
  }

  // extra: a loan does not touch the other books
  @Test
  void lend_doesNotChangeOtherBooks() {
    lend("dune", ANA);

    assertEquals(0, loanedCopies("hobbit"));
    assertEquals(2, library.allBooks().size());
  }

  // extra: loan ids are unique and increasing
  @Test
  void lend_assignsDistinctLoanIds() {
    var first = (LoanResult.Loaned) lend("dune", ANA);
    var second = (LoanResult.Loaned) lend("dune", DAN);

    assertNotEquals(first.loan().getId(), second.loan().getId());
  }
}