package com.library.backend.configurations;

import com.library.backend.dtos.requests.*;
import com.library.backend.dtos.responses.*;
import com.library.backend.services.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InitConfig {

    StudentService studentService;
    ManagerService managerService;
    BookService bookService;
    BorrowService borrowService;
    ReturnService returnService;
    UserService userService;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            List<UserDetailResponse> users = new ArrayList<>();
            List<ManagerDetailResponse> managers = new ArrayList<>();
            List<StudentDetailResponse> students = new ArrayList<>();
            List<BookDetailResponse> books = new ArrayList<>();
            List<BorrowDetailResponse> borrows = new ArrayList<>();
            List<ReturnDetailResponse> returns = new ArrayList<>();
            if (!userService.isInit()) {
                List<UserCreationRequest> requests = List.of(
                        new UserCreationRequest("user1", "pass123", "Nguyen Van A", "a@example.com", "0901000001"),
                        new UserCreationRequest("user2", "pass234", "Tran Thi B", "b@example.com", "0901000002"),
                        new UserCreationRequest("user3", "pass345", "Le Van C", "c@example.com", "0901000003"),
                        new UserCreationRequest("user4", "pass456", "Pham Thi D", "d@example.com", "0901000004"),
                        new UserCreationRequest("user5", "pass567", "Hoang Van E", "e@example.com", "0901000005")
                );
                for (UserCreationRequest request:requests) {
                    users.add(userService.create(request));
                }
            }
            if (!managerService.isInit()) {
                for (int i = 0; i < 2; i++) {
                    managers.add(managerService.create(ManagerCreationRequest.builder().userId(users.get(i).getId()).build()));
                }
            }
            if (!studentService.isInit()) {
                for (int i = 2; i < users.size(); i++) {
                    students.add(studentService.create(StudentCreationRequest.builder().userId(users.get(i).getId()).build()));
                }
            }
            if (!bookService.isInit()) {
                List<BookCreationRequest> requests = List.of(
                        new BookCreationRequest(
                                "Lập trình Java từ cơ bản đến nâng cao",
                                "Nguyen Van A",
                                "Công nghệ thông tin",
                                "NXB Trẻ",
                                "2020",
                                5,
                                "Sách hướng dẫn chi tiết lập trình Java.",
                                "https://book.sachgiai.com/uploads/book/sach-giao-khoa-tieng-viet-1-tap-1/tieng-viet-1-tap-1-0.jpg"
                        ),
                        new BookCreationRequest(
                                "Học Python hiệu quả",
                                "Tran Thi B",
                                "Công nghệ thông tin",
                                "NXB Giáo dục",
                                "2021",
                                3,
                                "Học Python nhanh chóng và thực hành nhiều bài tập.",
                                "https://book.sachgiai.com/uploads/book/sach-giao-khoa-tieng-viet-1-tap-1/tieng-viet-1-tap-1-0.jpg"
                        ),
                        new BookCreationRequest(
                                "Cấu trúc dữ liệu và giải thuật",
                                "Le Van C",
                                "Công nghệ thông tin",
                                "NXB Khoa học",
                                "2019",
                                4,
                                "Giải thích các cấu trúc dữ liệu và thuật toán cơ bản.",
                                "https://book.sachgiai.com/uploads/book/sach-giao-khoa-tieng-viet-1-tap-1/tieng-viet-1-tap-1-0.jpg"
                        ),
                        new BookCreationRequest(
                                "Thiết kế Web với HTML & CSS",
                                "Pham Thi D",
                                "Web Development",
                                "NXB Trẻ",
                                "2022",
                                6,
                                "Hướng dẫn thiết kế giao diện web chuyên nghiệp.",
                                "https://book.sachgiai.com/uploads/book/sach-giao-khoa-tieng-viet-1-tap-1/tieng-viet-1-tap-1-0.jpg"
                        ),
                        new BookCreationRequest(
                                "Machine Learning căn bản",
                                "Hoang Van E",
                                "AI & Machine Learning",
                                "NXB Giáo dục",
                                "2023",
                                2,
                                "Giới thiệu các khái niệm cơ bản về Machine Learning.",
                                "https://book.sachgiai.com/uploads/book/sach-giao-khoa-tieng-viet-1-tap-1/tieng-viet-1-tap-1-0.jpg"
                        )
                );
                for (BookCreationRequest request:requests) {
                    books.add(bookService.create(request));
                }
            }
            if (!borrowService.isInit()) {
                List<BorrowCreationRequest> requests = List.of(
                        BorrowCreationRequest.builder()
                                .studentId(students.get(0).getId())
                                .bookId(books.get(0).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(7))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(0).getId())
                                .bookId(books.get(1).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(10))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(1).getId())
                                .bookId(books.get(2).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(8))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(1).getId())
                                .bookId(books.get(3).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(12))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(2).getId())
                                .bookId(books.get(4).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(9))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(2).getId())
                                .bookId(books.get(0).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(11))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(0).getId())
                                .bookId(books.get(2).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(14))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(1).getId())
                                .bookId(books.get(1).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(13))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(2).getId())
                                .bookId(books.get(3).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(15))
                                .build(),
                        BorrowCreationRequest.builder()
                                .studentId(students.get(0).getId())
                                .bookId(books.get(4).getId())
                                .expectedReturnDate(LocalDate.now().plusDays(16))
                                .build()
                );

                for (BorrowCreationRequest request : requests) {
                    borrows.add(borrowService.create(request));
                }
            }
            if (!returnService.isInit()) {
                List<ReturnCreationRequest> returnRequests = List.of(
                        ReturnCreationRequest.builder()
                                .borrowId(borrows.get(0).getId())
                                .damagePercentage(0)
                                .build(),
                        ReturnCreationRequest.builder()
                                .borrowId(borrows.get(1).getId())
                                .damagePercentage(10)
                                .build(),
                        ReturnCreationRequest.builder()
                                .borrowId(borrows.get(2).getId())
                                .damagePercentage(0)
                                .build(),
                        ReturnCreationRequest.builder()
                                .borrowId(borrows.get(3).getId())
                                .damagePercentage(5)
                                .build(),
                        ReturnCreationRequest.builder()
                                .borrowId(borrows.get(4).getId())
                                .damagePercentage(0)
                                .build()
                );
                for (ReturnCreationRequest request : returnRequests) {
                    returns.add(returnService.create(request));
                }
            }
        };
    }

}
