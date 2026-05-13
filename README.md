# 💳 Banking Transaction System

A Spring Boot application that simulates real-world banking operations like account management and secure money transfers with concurrency handling.

---

## 🚀 Features

* Create Account
* Check Balance
* Transfer Money between accounts
* Transaction History
* Concurrency control using Optimistic Locking
* Global Exception Handling
* REST API with Swagger UI

---

## 🛠 Tech Stack

* Java 17
* Spring Boot
* Spring Data JPA
* MySQL
* Maven
* Lombok

---

## 🔐 Key Concepts Implemented

* @Transactional for atomic operations
* Optimistic Locking using @Version
* Layered Architecture (Controller → Service → Repository)
* DTO pattern for request/response
* Exception handling using @ControllerAdvice

---

## 📡 API Endpoints

### Create Account

POST /accounts

### Get Account

GET /accounts/{accountNumber}

### Transfer Money

POST /accounts/transfer

---

## ▶️ How to Run

1. Clone the repo
2. Configure MySQL in `application.yml`
3. Run the application
4. Access Swagger UI

---

## 📌 Future Improvements

* JWT Authentication
* Kafka integration for async processing
* Docker support
* Unit & Integration Tests

---

## 👤 Author

Bincy Nizam
