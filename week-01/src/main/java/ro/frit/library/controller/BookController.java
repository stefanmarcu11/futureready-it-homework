package ro.frit.library.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.frit.library.model.Book;
import ro.frit.library.service.Library;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

  private final Library library;

  @GetMapping
  public List<Book> all() {
    return library.allBooks();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> byId(@PathVariable String id) {
    return library.findBook(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElseGet(() -> {
              ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                      HttpStatus.NOT_FOUND, "Book not found: " + id);
              problem.setTitle("Book not found");
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
            });
  }
}
