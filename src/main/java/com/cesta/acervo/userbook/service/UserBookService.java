package com.cesta.acervo.userbook.service;

import com.cesta.acervo.book.entity.Book;
import com.cesta.acervo.book.enums.StatusReading;
import com.cesta.acervo.book.repository.BookRepository;
import com.cesta.acervo.book.service.BookService;
import com.cesta.acervo.user.entity.User;
import com.cesta.acervo.user.service.UserService;
import com.cesta.acervo.userbook.dto.UpdateUserBookRequest;
import com.cesta.acervo.userbook.dto.UserBookResponse;
import com.cesta.acervo.userbook.entity.UserBook;
import com.cesta.acervo.userbook.mapper.UserBookMapper;
import com.cesta.acervo.userbook.repository.UserBookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class UserBookService {

    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final UserService userService;
    private final UserBookMapper userBookMapper;

    public UserBookService(UserBookRepository userBookRepository,
                           BookRepository bookRepository,
                           BookService bookService,
                           UserService userService,
                           UserBookMapper userBookMapper) {
        this.userBookRepository = userBookRepository;
        this.bookRepository = bookRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.userBookMapper = userBookMapper;
    }

    @Transactional
    public UserBookResponse addByIsbn(Long userId, String isbn) {
        User user = userService.getById(userId);
        Long bookId = bookService.createByIsbn(isbn);

        if (userBookRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este livro já está no acervo do usuário");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado"));

        UserBook userBook = UserBook.builder()
                .user(user)
                .book(book)
                .statusReading(StatusReading.WANT_READ)
                .addedAt(Instant.now())
                .favorite(false)
                .build();

        return userBookMapper.toResponse(userBookRepository.save(userBook));
    }

    @Transactional
    public UserBookResponse update(Long userId, Long bookId, UpdateUserBookRequest request) {
        userService.getById(userId);
        UserBook userBook = userBookRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado no acervo"));

        if (request.statusReading() != null) {
            userBook.setStatusReading(request.statusReading());
        }
        if (request.startedAt() != null) {
            userBook.setStartedAt(request.startedAt());
        }
        if (request.finishedAt() != null) {
            userBook.setFinishedAt(request.finishedAt());
        }
        if (request.currentPage() != null) {
            userBook.setCurrentPage(request.currentPage());
        }
        if (request.rating() != null) {
            userBook.setRating(request.rating());
        }
        if (request.notes() != null) {
            userBook.setNotes(request.notes());
        }
        if (request.favorite() != null) {
            userBook.setFavorite(request.favorite());
        }

        return userBookMapper.toResponse(userBook);
    }

    @Transactional(readOnly = true)
    public List<UserBookResponse> listByUser(Long userId) {
        userService.getById(userId);
        return userBookRepository.findAllByUserIdWithBook(userId).stream()
                .map(userBookMapper::toResponse)
                .toList();
    }
}
