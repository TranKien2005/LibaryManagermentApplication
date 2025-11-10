package com.library.backend.mappers;

import com.library.backend.dtos.requests.BookCreationRequest;
import com.library.backend.dtos.requests.BookUpdateRequest;
import com.library.backend.dtos.responses.BookDetailResponse;
import com.library.backend.entities.Book;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BookMapper {

    Book toBook(BookCreationRequest request);

    BookDetailResponse toBookDetailResponse(Book book);

    void update(@MappingTarget Book book, BookUpdateRequest request);

}
