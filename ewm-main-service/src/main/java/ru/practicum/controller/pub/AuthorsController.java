package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.user.AuthorDto;
import ru.practicum.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/author")
public class AuthorsController {
    private final UserService service;

    @GetMapping
    public List<AuthorDto> getAuthors() {
        log.info("Controller: getAuthors()");
        return service.getAuthorsByRating();
    }
}