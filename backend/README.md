# Library Management Backend

Backend for the Library Management Application, built with **Spring Boot** and **PostgreSQL**, running via **Docker Compose**.

## Setup

1. **Create `.env` file**
   ```bash
   cp .env.example .env
   ```
   Edit values if needed (database name, user, password, etc.).

2. **Run with Docker**
   ```bash
   docker compose up --build -d
   ```

## APIs

- All failed api return response:
```json
{
   "success": false,
   "message": str,
   "data": None
}
```

### Auth

#### POST /auth
- Nhận username, password, trả về account_type
- Request body:
```json
{
   "username": str,
   "password": str
}
```
- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "acountType": "Student" | "Manager"
   }
}
   ```

### Student

#### GET students/{id}
- Get student's info by id
- id: student's id
- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "fullName": str,
      "email": str,
      "phone": str
   }
}
```

#### GET /students
- Get list of students with limit and offset (already have default value)
- Query params:
   - `limit` (default=10)
   - `offset` (default=0)
- Response:
```json
{
   "success": true,
   "message": str,
   "data": [
      {
         "id": int,
         "fullName": str,
         "email": str,
         "phone": str
      },
      ...
   ]
}
```

#### POST /students
- Create a student with account and info
- Request body:
```json
{
   "username": str,
   "password": str,
   "fullName": str,
   "email": str,
   "phone": str
}
```
- Response:
```json
{
   "success": true,
   "message": str,
   "data": None
}
```

#### PUT /students/{id}
- Update student's info
- id: student's id
- Request body:
```json
{
   "password": str | None,
   "fullName": str | None,
}
```
- Response body:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "fullName": str,
      "email": str,
      "phone": str
   }
}
```

#### DELETE /students/{id}
- Delete a student
- id: student's id
- Response:
```json
{
   "success": true,
   "message": str,
   "data": None
}
```

### Books

#### POST /books/data
- Create a book with data
- Request body:
```json
{
   "title": str,
   "author": str,
   "category": str,
   "publisher": str,
   "yearPublished": int,
   "availableCopies": int,
   "description": str,
   "rating": float,
   "numberOfRatings": int,
   "imageUrl": str
}
```
- Response body:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "title": str,
      "author": str,
      "category": str,
      "publisher": str,
      "yearPublished": int,
      "availableCopies": int,
      "description": str,
      "rating": float,
      "numberOfRatings": int,
      "imageUrl": str
   }
}
```

#### POST /books/isbn/{isbnCode}
- Create a book with isbn code
- isbnCode: book's code from google
- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "title": str,
      "author": str,
      "category": str,
      "publisher": str,
      "yearPublished": int,
      "availableCopies": int,
      "description": str,
      "rating": float,
      "numberOfRatings": int,
      "imageUrl": str
   }
}
```

#### GET /books/{id}
- Get book's info
- id: book's id
- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "title": str,
      "author": str,
      "category": str,
      "publisher": str,
      "yearPublished": int,
      "availableCopies": int,
      "description": str,
      "rating": float,
      "numberOfRatings": int,
      "imageUrl": str
   }
}
```

#### GET /books
- Get list of books with sorted, limit, offset
- Query params (all are optional):
   - `sorted` (example: rating)
   - `limit` (default=10)
   - `offset` (default=0)
- Response:
```json
{
   "success": true,
   "message": str,
   "data": [
      {
         "id": int,
         "title": str,
         "author": str,
         "category": str,
         "publisher": str,
         "yearPublished": int,
         "availableCopies": int,
         "description": str,
         "rating": float,
         "numberOfRatings": int,
         "imageUrl": str
      },
      ...
   ]
}
```

#### DELETE /books/{id}
- Delete a book
- id: book's id
- Response:
```json
{
   "success": true,
   "message": str,
   "data": None
}
```

### PUT /books/{id}
- Update a book
- id: book's id
- Request body:
```json
{
   "title": str | None,
   "author": str | None,
   "category": str | None,
   "publisher": str | None,
   "yearPublished": int | None,
   "availableCopies": int | None,
   "description": str | None,
   "imageUrl": str | None
}
```
- Response:
```json
{
   "success": true,
   "message": str,
   "data": [
      {
         "id": int,
         "title": str,
         "author": str,
         "category": str,
         "publisher": str,
         "yearPublished": int,
         "availableCopies": int,
         "description": str,
         "rating": float,
         "numberOfRatings": int,
         "imageUrl": str
      },
      ...
   ]
}
```

### Borrow

#### GET /borrows/student/{id}
- Get list of borrowing's info of a student
-id: student's id
- Response:
```json
{
   "success": true,
   "message": str,
   "data": [
      {
         "id": int,
         "student": {
            "id": int,
            "fullName": str
         },
         "book": {
            "id": int,
            "title": str,
            "imageUrl": str
         },
         "borrowDate": Date,
         "expectedReturnDate": Date,
         "status": "Borrowed" | "Returned"
      },
      ...
   ]
}
```

#### GET /borrows/{id}
- Get a borrowing's info
- id: borrow's id
- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "student": {
         "id": int,
         "fullName": str
      },
      "book": {
         "id": int,
         "title": str,
         "imageUrl": str
      },
      "borrowDate": Date,
      "expectedReturnDate": Date,
      "status": "Borrowed" | "Returned"
   }
}
```

#### GET /borrows/check
- Check if student borrowed book
- Request params:
   - `studentId`
   - `bookId`
- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "isBorrowing": bool
   }
}
```

#### POST /borrows
- Create a borrowing
- Request body:
```json
{
   "studentId": int,
   "bookId": int
}
```
- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "student": {
         "id": int,
         "fullName": str
      },
      "book": {
         "id": int,
         "title": str,
         "imageUrl": str
      },
      "borrowDate": Date,
      "expectedReturnDate": Date,
      "status": "Borrowed" | "Returned"
   }
}
```

### Return

#### POST /returns
- Return book
- Request body:
```json
{
   "borrowId": int,
   "returnDate": Date,
   "damagePercentage": int
}
```

- Response:
```json
{
   "success": true,
   "message": str,
   "data": {
      "id": int,
      "borrow": {
         "id": int,
         "student": {
            "id": int,
            "fullName": str
         },
         "book": {
            "id": int,
            "title": str,
            "imageUrl": str
         },
         "borrowDate": Date,
         "expectedReturnDate": Date,
         "status": "Borrowed" | "Returned"
      },
      "returnDate": Date,
      "damagePercentage": int
   }
}
```