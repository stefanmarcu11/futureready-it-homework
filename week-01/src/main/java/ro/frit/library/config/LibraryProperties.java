package ro.frit.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "library")
public record LibraryProperties(List<SeedBook> seedBooks) {

  public LibraryProperties {
    seedBooks = seedBooks == null ? List.of() : List.copyOf(seedBooks);
  }

  public record SeedBook(String id, String title, String author, int copies) {}
}
