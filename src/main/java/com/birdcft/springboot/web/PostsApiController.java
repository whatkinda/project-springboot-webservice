package com.birdcft.springboot.web;

import com.birdcft.springboot.web.dto.PostsResponseDto;
import com.birdcft.springboot.web.service.PostsService;
import com.birdcft.springboot.web.dto.PostsSaveRequestDto;
import com.birdcft.springboot.web.dto.PostsUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;

@RequiredArgsConstructor
@Controller
public class PostsApiController {

    private final PostsService postsService;

    @PostMapping("/api/v1/posts")
    public ResponseEntity<Long> save(@RequestBody PostsSaveRequestDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        Long result = postsService.save(requestDto);
        if (result > 0L) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/api/v1/posts/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        Long result = postsService.update(id, requestDto);

        if (result > 0L) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/api/v1/posts/{id}")
    public @ResponseBody PostsResponseDto findById(@PathVariable Long id) {
        return postsService.findById(id);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        postsService.delete(id);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
