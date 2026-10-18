package ro.frit.library.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LibraryPropertiesTest {

  @Test
  void missingSeedBooks_becomesEmptyList() {
    assertEquals(List.of(), new LibraryProperties(null).seedBooks());
  }
}
