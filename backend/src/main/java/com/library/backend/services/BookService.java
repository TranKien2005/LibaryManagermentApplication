package com.library.backend.services;

import com.library.backend.dtos.requests.BookCreationRequest;
import com.library.backend.dtos.requests.BookUpdateRequest;
import com.library.backend.dtos.requests.v1.Document;
import com.library.backend.dtos.responses.BookDetailResponse;
import com.library.backend.entities.Book;
import com.library.backend.entities.Borrow;
import com.library.backend.exceptions.GeneralException;
import com.library.backend.exceptions.ResponseCode;
import com.library.backend.mappers.BookMapper;
import com.library.backend.repositories.BookRepository;
import com.library.backend.repositories.BorrowRepository;
import com.library.backend.repositories.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {

    BookRepository bookRepository;
    StudentRepository studentRepository;
    BorrowRepository borrowRepository;
    BookMapper bookMapper;
//    RedisTemplate<String, Object> redisTemplate;
    RedissonClient redissonClient;
    String TRENDING_KEY = "trending_book_ids";
    int CACHE_LIMIT = 100; // cache top 100 book IDs
    long CACHE_TTL_MINUTES = 60;

    public List<BookDetailResponse> getAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toBookDetailResponse).toList();
    }

    public List<BookDetailResponse> init_getAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(bookMapper::toBookDetailResponse).toList();
    }

    public void delete(Integer id) {
        bookRepository.deleteById(id);
    }

    public BookDetailResponse create(BookCreationRequest request) {
        Book book = bookMapper.toBook(request);
        book.setRating(0.0);
        book.setReviewCount(0);
        book = bookRepository.save(book);
        return bookMapper.toBookDetailResponse(book);
    }

    public BookDetailResponse init_create(BookCreationRequest request) {
        Book book = bookMapper.toBook(request);
        book.setRating(0.0);
        book.setReviewCount(0);
        book = bookRepository.save(book);
        return bookMapper.toBookDetailResponse(book);
    }

    public BookDetailResponse update(Integer id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.BOOK_NOT_FOUND));
        bookMapper.update(book, request);
        book = bookRepository.save(book);
        return bookMapper.toBookDetailResponse(book);
    }

    public List<Integer> getAllIds() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(Book::getId).toList();
    }

    public BookDetailResponse getById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.BOOK_NOT_FOUND));
        return bookMapper.toBookDetailResponse(book);
    }

    public BookDetailResponse addRating(Integer id, Integer newRating) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ResponseCode.BOOK_NOT_FOUND));
        Integer count = book.getReviewCount();
        book.setRating((count * book.getRating() + newRating) / (count + 1));
        book.setReviewCount(count + 1);
        book = bookRepository.save(book);
        return bookMapper.toBookDetailResponse(book);
    }

    public Page<BookDetailResponse> getBooks(Pageable pageable) {
        Page<Book> page = bookRepository.findAll(pageable);
        return page.map(bookMapper::toBookDetailResponse);
    }

    public List<BookDetailResponse> getRecommendedBooks(Integer studentId, int limit) {
        List<Borrow> borrows = borrowRepository.findByStudentUserId(studentId);
        Map<Book, Long> borrowCountMap = borrows.stream()
                .collect(Collectors.groupingBy(Borrow::getBook, Collectors.counting()));
        Map<String, Long> categoryScore = borrowCountMap.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getKey().getCategory(),
                        Collectors.summingLong(Map.Entry::getValue)
                ));
        Map<String, Long> authorScore = borrowCountMap.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getKey().getAuthor(),
                        Collectors.summingLong(Map.Entry::getValue)
                ));
        List<Book> books = bookRepository.findAll();
        Set<Integer> borrowedIds = borrows.stream()
                .map(b -> b.getBook().getId())
                .collect(Collectors.toSet());
        List<Book> recommendedBooks = books.stream()
                .filter(book -> !borrowedIds.contains(book.getId()))
                .map(book -> Map.entry(
                        book,
                        categoryScore.getOrDefault(book.getCategory(), 0L)
                                + authorScore.getOrDefault(book.getAuthor(), 0L)
                ))
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
        return recommendedBooks.stream().map(bookMapper::toBookDetailResponse).toList();
    }

    public List<BookDetailResponse> getTrending(int limit) {
//        List<Integer> cachedIds = (List<Integer>) redisTemplate.opsForValue().get(TRENDING_KEY);

        RBucket<List<Integer>> bucket = redissonClient.getBucket(TRENDING_KEY);
        List<Integer> cachedIds = bucket.get();
        // 2️⃣ Nếu cache có, dùng luôn
        if (cachedIds != null && !cachedIds.isEmpty()) {
            List<Integer> topIds = cachedIds.stream()
                    .limit(Math.min(limit, CACHE_LIMIT))
                    .toList();

            // Lấy các book trong top 100
            List<Book> cachedBooks = bookRepository.findAllById(topIds);

            // Giữ đúng thứ tự
            Map<Integer, Book> bookMap = cachedBooks.stream()
                    .collect(Collectors.toMap(Book::getId, b -> b));

            List<Book> orderedTopBooks = topIds.stream()
                    .map(bookMap::get)
                    .filter(Objects::nonNull)
                    .toList();

            // 3️⃣ Nếu limit <= 100 → done luôn
            if (limit <= CACHE_LIMIT) {
                return orderedTopBooks.stream()
                        .map(bookMapper::toBookDetailResponse)
                        .toList();
            }

            // 4️⃣ Nếu limit > 100 → lấy thêm ngoài cache
            int extraLimit = limit - CACHE_LIMIT;
            List<Integer> cachedSet = new ArrayList<>(cachedIds);

            List<Book> extraBooks = bookRepository.findByIdNotInOrderByRatingDesc(cachedSet, PageRequest.of(0, extraLimit));

            // Gộp lại (top + extra)
            List<Book> result = new ArrayList<>();
            result.addAll(orderedTopBooks);
            result.addAll(extraBooks);

            return result.stream().map(bookMapper::toBookDetailResponse).toList();
        }

        // 5️⃣ Cache chưa có → tính toán lại
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<Book> books = bookRepository.findAll();
        List<Borrow> recentBorrows = borrowRepository.findByBorrowDateAfter(oneMonthAgo);

        Map<Integer, Long> borrowCountMap = recentBorrows.stream()
                .collect(Collectors.groupingBy(b -> b.getBook().getId(), Collectors.counting()));

        List<Book> sortedBooks = books.stream()
                .sorted((b1, b2) -> {
                    long count1 = borrowCountMap.getOrDefault(b1.getId(), 0L);
                    long count2 = borrowCountMap.getOrDefault(b2.getId(), 0L);

                    if (count1 != count2)
                        return Long.compare(count2, count1); // borrow_count desc
                    return Double.compare(b2.getRating(), b1.getRating()); // rating desc
                })
                .toList();

        // Cache top 100 IDs
        List<Integer> topIds = sortedBooks.stream()
                .limit(CACHE_LIMIT)
                .map(Book::getId)
                .toList();

        RBucket<List<Integer>> bucket2 = redissonClient.getBucket(TRENDING_KEY);
        bucket2.set(topIds, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
//        redisTemplate.opsForValue().set(TRENDING_KEY, topIds, CACHE_TTL_MINUTES, TimeUnit.MINUTES);

        // Trả về theo limit
        return sortedBooks.stream()
                .limit(limit)
                .map(bookMapper::toBookDetailResponse)
                .toList();
//        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
//        List<Book> books = bookRepository.findAll();
//        List<Borrow> recentBorrows = borrowRepository.findByBorrowDateAfter(oneMonthAgo);
//        Map<Integer, Long> borrowCountMap = recentBorrows.stream()
//                .collect(Collectors.groupingBy(b -> b.getBook().getId(), Collectors.counting()));
//        books = books.stream()
//                .sorted((b1, b2) -> {
//                    long count1 = borrowCountMap.getOrDefault(b1.getId(), 0L);
//                    long count2 = borrowCountMap.getOrDefault(b2.getId(), 0L);
//
//                    if (count1 != count2) {
//                        return Long.compare(count2, count1); // borrow_count desc
//                    }
//                    return Double.compare(b2.getRating(), b1.getRating()); // rating desc
//                })
//                .limit(limit)
//                .toList();
//        return books.stream().map(bookMapper::toBookDetailResponse).toList();
    }

    public List<BookDetailResponse> search(String key, int page, int pageSize) {
        List<Book> allBooks = bookRepository.findAll();

        String keyword = key.toLowerCase();

        List<Book> books = allBooks.stream()
                .filter(b -> {
                    String title = b.getTitle() == null ? "" : b.getTitle().toLowerCase();
                    String category = b.getCategory() == null ? "" : b.getCategory().toLowerCase();
                    String author = b.getAuthor() == null ? "" : b.getAuthor().toLowerCase();
                    String desc = b.getDescription() == null ? "" : b.getDescription().toLowerCase();

                    return title.contains(keyword)
                            || category.contains(keyword)
                            || author.contains(keyword)
                            || desc.contains(keyword);
                })
                .sorted((b1, b2) -> {
                    int rank1 =
                            b1.getTitle().toLowerCase().contains(keyword) ? 1 :
                                    b1.getCategory().toLowerCase().contains(keyword) ? 2 :
                                            b1.getAuthor().toLowerCase().contains(keyword) ? 3 :
                                                    b1.getDescription().toLowerCase().contains(keyword) ? 4 : 5;

                    int rank2 =
                            b2.getTitle().toLowerCase().contains(keyword) ? 1 :
                                    b2.getCategory().toLowerCase().contains(keyword) ? 2 :
                                            b2.getAuthor().toLowerCase().contains(keyword) ? 3 :
                                                    b2.getDescription().toLowerCase().contains(keyword) ? 4 : 5;

                    if (rank1 != rank2) return Integer.compare(rank1, rank2);

                    return Integer.compare(
                            b2.getYearPublished(), b1.getYearPublished()
                    );
                })
                .skip((long) page * pageSize)
                .limit(pageSize)
                .toList();
        return books.stream().map(bookMapper::toBookDetailResponse).toList();
    }

    public BookDetailResponse getByTitleAuthorCategoryPublisherYearPublishedAvailableCopies(
            String title,
            String author,
            String category,
            String publisher,
            Integer yearPublished,
            Integer availableCopies
    ) {
        Book book = bookRepository.findByTitleAndAuthorAndCategoryAndPublisherAndYearPublishedAndAvailableCopies(
                title, author, category, publisher, yearPublished, availableCopies
        ).orElseThrow(() -> new GeneralException(ResponseCode.BOOK_NOT_FOUND));
        return bookMapper.toBookDetailResponse(book);
    }

    public boolean isInit() {
        return bookRepository.count() > 0;
    }

    public Page<BookDetailResponse> getByYearPublished(Pageable pageable) {
        Page<Book> books = bookRepository.findAllByOrderByYearPublishedDesc(pageable);
        return books.map(bookMapper::toBookDetailResponse);
    }

}
