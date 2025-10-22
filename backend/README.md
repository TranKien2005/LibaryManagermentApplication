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

## Auth

### POST /api/auth/login
- This api for getting access_token and refresh_token
- access_token can live 30 minutes, and refresh_token can live 1 week

Request:
```json
{
  "username": str,
  "password": str
}
```
Response:
```json
{
  "accountType": "User" | "Manager",
  "accessToken": str,
  "refreshToken": str
}
```

### POST /api/auth/refresh
- This api for getting new access_token when it outdate

Request:
```json
{
  "refreshToken": str
}
```

Response:
```json
{
  "accountType": str,
  "accessToken": str,
  "refreshToken": str
}
```


### POST /api/auth/logout
- This api for removing refresh_token from system

Request:
```json
{
  "refreshToken": str
}
```
Response:
```json
null
```

