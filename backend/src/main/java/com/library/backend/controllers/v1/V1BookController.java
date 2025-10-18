package com.library.backend.controllers.v1;

import com.library.backend.dtos.requests.BookCreationRequest;
import com.library.backend.dtos.requests.BookUpdateRequest;
import com.library.backend.dtos.requests.v1.Document;
import com.library.backend.dtos.responses.ApiResponse;
import com.library.backend.dtos.responses.BookDetailResponse;
import com.library.backend.services.BookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class V1BookController {

    BookService bookService;

    @GetMapping
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll() {
        List<BookDetailResponse> list = bookService.getAll();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(response -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", response.getTitle());
                    map.put("author", response.getAuthor());
                    map.put("category", response.getCategory());
                    map.put("publisher", response.getPublisher());
                    map.put("yearPublished", response.getYearPublished());
                    map.put("availableCopies", response.getAvailableCopies());
                    map.put("bookID", response.getId());
                    map.put("description", response.getDescription());
                    map.put("rating", response.getRating());
                    map.put("reviewCount", response.getReviewCount());
                    map.put("coverImageUrl", response.getCoverImageUrl());
                    return map;
                }).toList()
        ));
    }

    @PostMapping
    ResponseEntity<ApiResponse<Void>> insert(
            @RequestBody Map<String, Object> request
    ) {
        BookCreationRequest bookCreationRequest = BookCreationRequest.builder()
                .title((String) request.get("title"))
                .author((String) request.get("author"))
                .category((String) request.get("category"))
                .publisher((String) request.get("publisher"))
                .yearPublished((Integer) request.get("yearPublished"))
                .availableCopies((Integer) request.get("availableCopies"))
                .description((String) request.get("description"))
                .coverImageUrl((String) request.get("coverImageUrl"))
                .build();
        BookDetailResponse response = bookService.create(bookCreationRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> update(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        BookUpdateRequest bookUpdateRequest = BookUpdateRequest.builder()
                .title((String) request.get("title"))
                .description((String) request.get("description"))
                .publisher((String) request.get("publisher"))
                .yearPublished((Integer) request.get("yearPublished"))
                .coverImageUrl((String) request.get("coverImageUrl"))
                .author((String) request.get("author"))
                .availableCopies((Integer) request.get("availableCopies"))
                .category((String) request.get("category"))
                .build();
        BookDetailResponse response = bookService.update(id, bookUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {
        bookService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<Map<String, Object>>> get(
            @PathVariable Integer id
    ) {
        BookDetailResponse response = bookService.getById(id);
        Map<String, Object> map = new HashMap<>();
        map.put("title", response.getTitle());
        map.put("author", response.getAuthor());
        map.put("category", response.getCategory());
        map.put("publisher", response.getPublisher());
        map.put("yearPublished", response.getYearPublished());
        map.put("availableCopies", response.getAvailableCopies());
        map.put("bookID", response.getId());
        map.put("description", response.getDescription());
        map.put("rating", response.getRating());
        map.put("reviewCount", response.getReviewCount());
        map.put("coverImageUrl", response.getCoverImageUrl());
        return ResponseEntity.ok().body(ApiResponse.success(map));
    }

    @PostMapping("/get-id")
    ResponseEntity<ApiResponse<Integer>>getID(
            @RequestBody Map<String, Object> request
    ) {
        BookDetailResponse response = bookService.getByTitleAuthorCategoryPublisherYearPublishedAvailableCopies(
                (String) request.get("title"),
                (String) request.get("author"),
                (String) request.get("category"),
                (String) request.get("publisher"),
                (Integer) request.get("yearPublished"),
                (Integer) request.get("availableCopies")
        );
        return ResponseEntity.ok().body(ApiResponse.success(response.getId()));
    }

    @GetMapping("/ids")
    ResponseEntity<ApiResponse<List<Integer>>> getAllId() {
        List<Integer> ids = bookService.getAllIds();
        return ResponseEntity.ok().body(ApiResponse.success(ids));
    }

    @PutMapping("/{id}/image-by-url")
    ResponseEntity<ApiResponse<Void>> setBookImageUrl(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        String imageUrl = (String) request.get("imageUrl");
        BookUpdateRequest bookUpdateRequest = BookUpdateRequest.builder()
                .coverImageUrl(imageUrl)
                .build();
        BookDetailResponse response = bookService.update(id, bookUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}/image")
    ResponseEntity<ApiResponse<Void>> setBookImage(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        String imagePath = (String) request.get("imagePath");
        BookUpdateRequest bookUpdateRequest = BookUpdateRequest.builder()
                .coverImageUrl(imagePath)
                .build();
        BookDetailResponse response = bookService.update(id, bookUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}/description")
    ResponseEntity<ApiResponse<Void>> setDescription(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        String description = (String) request.get("description");
        BookUpdateRequest bookUpdateRequest = BookUpdateRequest.builder()
                .description(description)
                .build();
        BookDetailResponse response = bookService.update(id, bookUpdateRequest);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @PutMapping("/{id}/rating")
    ResponseEntity<ApiResponse<Void>> addRating(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request
    ) {
        Integer newRating = (Integer) request.get("rating");
        BookDetailResponse response = bookService.addRating(id, newRating);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }

    @GetMapping("/top-rated")
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopRatedBooks(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset
    ) {
        Pageable pageable = PageRequest.of(offset, limit, Sort.by("rating").descending());
        Page<BookDetailResponse> books = bookService.getBooks(pageable);
        List<BookDetailResponse> list = books.getContent();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(response -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", response.getTitle());
                    map.put("author", response.getAuthor());
                    map.put("category", response.getCategory());
                    map.put("publisher", response.getPublisher());
                    map.put("yearPublished", response.getYearPublished());
                    map.put("availableCopies", response.getAvailableCopies());
                    map.put("bookID", response.getId());
                    map.put("description", response.getDescription());
                    map.put("rating", response.getRating());
                    map.put("reviewCount", response.getReviewCount());
                    map.put("coverImageUrl", response.getCoverImageUrl());
                    return map;
                }).toList()
        ));
    }

    @GetMapping("/favorite")
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFavorite(
            @RequestParam Integer accountId
    ) {
        List<BookDetailResponse> list = bookService.getRecommendedBooks(accountId, 10);
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(response -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", response.getTitle());
                    map.put("author", response.getAuthor());
                    map.put("category", response.getCategory());
                    map.put("publisher", response.getPublisher());
                    map.put("yearPublished", response.getYearPublished());
                    map.put("availableCopies", response.getAvailableCopies());
                    map.put("bookID", response.getId());
                    map.put("description", response.getDescription());
                    map.put("rating", response.getRating());
                    map.put("reviewCount", response.getReviewCount());
                    map.put("coverImageUrl", response.getCoverImageUrl());
                    return map;
                }).toList()
        ));
    }

    @GetMapping("/trending")
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTrending() {
        List<BookDetailResponse> list = bookService.getTrending();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(response -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", response.getTitle());
                    map.put("author", response.getAuthor());
                    map.put("category", response.getCategory());
                    map.put("publisher", response.getPublisher());
                    map.put("yearPublished", response.getYearPublished());
                    map.put("availableCopies", response.getAvailableCopies());
                    map.put("bookID", response.getId());
                    map.put("description", response.getDescription());
                    map.put("rating", response.getRating());
                    map.put("reviewCount", response.getReviewCount());
                    map.put("coverImageUrl", response.getCoverImageUrl());
                    return map;
                }).toList()
        ));
    }

//    @GetMapping
//    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
//            @RequestParam Integer page,
//            @RequestParam Integer pageSize
//    ) {
//        Pageable pageable = PageRequest.of(page, pageSize);
//        Page<BookDetailResponse> p = bookService.getBooks(pageable);
//        List<BookDetailResponse> list = p.getContent();
//        return ResponseEntity.ok().body(ApiResponse.success(
//                list.stream().map(response -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("title", response.getTitle());
//                    map.put("author", response.getAuthor());
//                    map.put("category", response.getCategory());
//                    map.put("publisher", response.getPublisher());
//                    map.put("yearPublished", response.getYearPublished());
//                    map.put("availableCopies", response.getAvailableCopies());
//                    map.put("bookID", response.getId());
//                    map.put("description", response.getDescription());
//                    map.put("rating", response.getRating());
//                    map.put("reviewCount", response.getReviewCount());
//                    map.put("coverImageUrl", response.getCoverImageUrl());
//                    return map;
//                }).toList()
//        ));
//    }

    @GetMapping("/search")
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> search(
            @RequestParam String query,
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        List<BookDetailResponse> list = bookService.search(query, page, size);
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(response -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", response.getTitle());
                    map.put("author", response.getAuthor());
                    map.put("category", response.getCategory());
                    map.put("publisher", response.getPublisher());
                    map.put("yearPublished", response.getYearPublished());
                    map.put("availableCopies", response.getAvailableCopies());
                    map.put("bookID", response.getId());
                    map.put("description", response.getDescription());
                    map.put("rating", response.getRating());
                    map.put("reviewCount", response.getReviewCount());
                    map.put("coverImageUrl", response.getCoverImageUrl());
                    return map;
                }).toList()
        ));
    }

    @GetMapping("/new-arrivals")
    ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNewArrivals(
            @RequestParam Integer page,
            @RequestParam Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("yearPublished").descending());
        Page<BookDetailResponse> books = bookService.getBooks(pageable);
        List<BookDetailResponse> list = books.getContent();
        return ResponseEntity.ok().body(ApiResponse.success(
                list.stream().map(response -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", response.getTitle());
                    map.put("author", response.getAuthor());
                    map.put("category", response.getCategory());
                    map.put("publisher", response.getPublisher());
                    map.put("yearPublished", response.getYearPublished());
                    map.put("availableCopies", response.getAvailableCopies());
                    map.put("bookID", response.getId());
                    map.put("description", response.getDescription());
                    map.put("rating", response.getRating());
                    map.put("reviewCount", response.getReviewCount());
                    map.put("coverImageUrl", response.getCoverImageUrl());
                    return map;
                }).toList()
        ));
    }



}
