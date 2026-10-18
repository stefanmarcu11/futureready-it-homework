package ro.frit.library.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ro.frit.library.service.Library;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("local")
public class LibraryConfigTest {

  private final Library library;

  LibraryConfigTest(Library library) {
    this.library = library;
  }

  @Test
  void localProfile_seedsDuneAndHobbit() {
    assertEquals(2, library.allBooks().size());
    assertEquals(3, library.findBook("dune").orElseThrow().getCopies());
    assertEquals(1, library.findBook("hobbit").orElseThrow().getCopies());
  }
}
