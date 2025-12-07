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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InitConfig {

    DataInit init;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> init.initData();
    }

    @Component
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static class DataInit {
        StudentService studentService;
        ManagerService managerService;
        BookService bookService;
        BorrowService borrowService;
        ReturnService returnService;
        UserService userService;
        @Transactional
        void initData() {
            List<UserDetailResponse> users = new ArrayList<>();
            List<ManagerDetailResponse> managers = new ArrayList<>();
            List<StudentDetailResponse> students = new ArrayList<>();
            List<BookDetailResponse> books = new ArrayList<>();
            List<BorrowDetailResponse> borrows = new ArrayList<>();
            List<ReturnDetailResponse> returns = new ArrayList<>();

            // --- Users ---
            if (!userService.isInit()) {
                System.out.println("Init 100000 users");
                for (int i = 1; i <= 100000; i++) {
                    String username = "user_number_" + i;
                    String password = "pass_number_" + i;
                    String name = "Nguyen Van " + i;
                    String email = "user" + i + "@example.com";
                    String phone = "0901" + String.format("%06d", i);
                    UserCreationRequest req = new UserCreationRequest(username, password, name, email, phone);
                    users.add(userService.create(req));
                }
            } else {
                System.out.println("Fetch user");
                users = userService.init_getAll();
            }

            // --- Managers ---
//            if (!managerService.isInit()) {
//                System.out.println("Init 10 managers");
//                for (int i = 0; i < 10; i++) {
//                    managers.add(managerService.init_create(
//                            ManagerCreationRequest.builder().userId(users.get(i).getId()).build()
//                    ));
//                }
//            } else {
//                System.out.println("Fetch manager");
//                managers = managerService.init_getAll();
//            }
//
//            // --- Students ---
//            if (!studentService.isInit()) {
//                System.out.println("Init students");
//                for (int i = 10; i < users.size(); i++) { // phần còn lại là học sinh
//                    students.add(studentService.init_create(
//                            StudentCreationRequest.builder().userId(users.get(i).getId()).build()
//                    ));
//                }
//            } else {
//                System.out.println("Fetch student");
//                students = studentService.init_getAll();
//            }
//
//            // --- Books ---
//            if (!bookService.isInit()) {
//                System.out.println("Init 10.000 books");
//                String[] categories = {"Công nghệ thông tin", "Web Development", "AI & Machine Learning", "Database", "Mobile Development"};
//                String cover = "https://book.sachgiai.com/uploads/book/default.jpg";
//
//                for (int i = 1; i <= 10000; i++) {
//                    String title = "Book " + i;
//                    String author = "Author " + (i % 1000 + 1); // lặp tác giả cho 1000 user
//                    String category = categories[i % categories.length];
//                    String publisher = "NXB " + (i % 50 + 1); // 50 nhà xuất bản khác nhau
//                    int year = 2000 + (i % 24);
//                    int quantity = 1 + (i % 10);
//                    String description = "Mô tả sách " + i;
//
//                    BookCreationRequest req = new BookCreationRequest(title, author, category, publisher, year, quantity, description, cover);
//                    books.add(bookService.init_create(req));
//                }
//            } else {
//                System.out.println("Fetch book");
//                books = bookService.init_getAll();
//            }
//
//            // --- Borrows ---
//            if (!borrowService.isInit()) {
//                System.out.println("Init borrows");
//                int borrowCount = Math.min(students.size(), books.size());
//                for (int i = 0; i < borrowCount; i++) {
//                    BorrowCreationRequest req = BorrowCreationRequest.builder()
//                            .studentId(students.get(i % students.size()).getId())
//                            .bookId(books.get(i % books.size()).getId())
//                            .expectedReturnDate(LocalDate.now().plusDays(7 + i % 30))
//                            .build();
//                    borrows.add(borrowService.init_create(req));
//                }
//            } else {
//                System.out.println("Fetch borrow");
//                borrows = borrowService.init_getAll();
//            }
//
//            // --- Returns ---
//            if (!returnService.isInit()) {
//                System.out.println("Init returns");
//                for (int i = 0; i < borrows.size(); i++) {
//                    ReturnCreationRequest req = ReturnCreationRequest.builder()
//                            .borrowId(borrows.get(i).getId())
//                            .damagePercentage(i % 10 == 0 ? 0 : (i % 5) * 5)
//                            .build();
//                    returns.add(returnService.init_create(req));
//                }
//            } else {
//                System.out.println("Fetch return");
//                returns = returnService.init_getAll();
//            }
        }

    }




}
