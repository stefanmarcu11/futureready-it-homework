package ro.frit.library.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.frit.library.model.Book;
import ro.frit.library.service.Library;

import java.util.List;

@Configuration
@EnableConfigurationProperties(LibraryProperties.class)
public class LibraryConfig {

  @Bean
  public Library library(LibraryProperties properties) {
    List<Book> books = properties.seedBooks().stream()
            .map(seed -> Book.builder()
                    .id(seed.id())
                    .title(seed.title())
                    .author(seed.author())
                    .copies(seed.copies())
                    .loanedCopies(0)
                    .build())
            .toList();
    return new Library(books);
  }
}
