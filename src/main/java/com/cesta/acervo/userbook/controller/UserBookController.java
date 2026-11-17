package com.cesta.acervo.userbook.controller;

import com.cesta.acervo.userbook.dto.UpdateUserBookRequest;
import com.cesta.acervo.userbook.dto.UserBookResponse;
import com.cesta.acervo.userbook.service.UserBookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/books")
@Validated
public class UserBookController {

    private final UserBookService userBookService;

    public UserBookController(UserBookService userBookService) {
        this.userBookService = userBookService;
    }

    @PostMapping("/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserBookResponse add(@PathVariable Long userId, @PathVariable Long bookId) {
        return userBookService.add(userId, bookId);
    }

    @PatchMapping("/{bookId}")
    public UserBookResponse update(@PathVariable Long userId,
                                   @PathVariable Long bookId,
                                   @Valid @RequestBody UpdateUserBookRequest request) {
        return userBookService.update(userId, bookId, request);
    }

    @GetMapping
    public List<UserBookResponse> list(@PathVariable Long userId) {
        return userBookService.listByUser(userId);
    }
}
