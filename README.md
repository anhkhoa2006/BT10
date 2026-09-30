# BT10 - Demo JWT với Spring Boot 3 & Spring Security 6

Dự án triển khai bài tập thực hành JWT (JSON Web Token) dựa trên bài giảng của ThS. Nguyễn Hữu Trung - Trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE).

## 🚀 Công nghệ sử dụng
- **Java 17+**
- **Spring Boot 3.3.4**
- **Spring Security 6** (Stateless Session Policy, Custom OncePerRequestFilter)
- **Spring Data JPA & Hibernate**
- **JJWT 0.12.6** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- **MySQL / H2 Database**
- **Thymeleaf & AJAX (jQuery, Bootstrap 5)**

## 📂 Cấu trúc thư mục
- `vn.iotstar.configs`: Cấu hình Security (`SecurityConfiguration`), Bean xác thực (`ApplicationConfiguration`)
- `vn.iotstar.controllers`:
  - `AuthenticationController`: API xác thực (`/auth/signup`, `/auth/login`)
  - `UserController`: API bảo vệ (`/users/me`, `/users`)
  - `AuthController`: Render giao diện (`/login`, `/user/profile`)
- `vn.iotstar.entity`: `User` entity implements `UserDetails`
- `vn.iotstar.exceptions`: `GlobalExceptionHandler` xử lý lỗi xác thực và JWT ngoại lệ
- `vn.iotstar.filter`: `JwtAuthenticationFilter` chặn request và trích xuất `Bearer <token>`
- `vn.iotstar.models`: DTO (`LoginResponse`, `LoginUserModel`, `RegisterUserModel`)
- `vn.iotstar.repository`: `UserRepository`
- `vn.iotstar.services`: `JwtService`, `AuthenticationService`, `UserService`
- `src/main/resources/templates`: `login.html`, `profile.html`
- `src/main/resources/static/js`: `mainjs.js`

## 🛠️ Hướng dẫn chạy ứng dụng
1. Cấu hình cơ sở dữ liệu trong `src/main/resources/application.properties`.
2. Chạy ứng dụng bằng lệnh:
   ```bash
   mvn spring-boot:run
   ```
3. Truy cập giao diện:
   - Đăng nhập: `http://localhost:8005/login`
   - Thông tin cá nhân: `http://localhost:8005/user/profile`
