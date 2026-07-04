# 💳 Banking Transaction System

A Spring Boot application that simulates real-world banking operations like account management and secure money transfers with concurrency handling.

---

## 💼 Business Context

Money transfer is one of the highest-risk operations in any banking system — errors
here translate directly into financial loss, regulatory exposure, or reconciliation
disputes.

This project models the core risk that any real transfer engine must solve:
**preventing double-processing or balance corruption when two transfer requests touch
the same account concurrently** (e.g., a customer double-tapping "transfer" on a flaky
network, or two scheduled payments hitting an account at once).

In a regulated environment, getting this wrong has consequences beyond a bug report:
- **Double-spend risk** — a race condition could let an account spend the same funds twice
- **Audit/reconciliation failures** — inconsistent balances trigger manual investigation,
  which is costly and erodes customer trust
- **Regulatory exposure** — financial regulators (e.g., SAMA in Saudi Arabia, or
  equivalent bodies) expect demonstrable controls around transaction integrity

This system's concurrency control, transactional boundaries, and structured error
handling are the technical building blocks that satisfy those non-functional
requirements — the same category of NFRs (consistency, auditability, fault isolation)
that show up in any capital markets or payments RFP.

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
* JWT Authentication
  
---

## 📐 Design Decisions

### Why Optimistic Locking over Pessimistic Locking
Money transfers between accounts are a classic concurrency hotspot — two simultaneous
transfers against the same account can corrupt the balance if not handled carefully.

This project uses **optimistic locking** (`@Version` on the Account entity) rather than
pessimistic locking (`SELECT ... FOR UPDATE`) because:
- Transfer requests are expected to be frequent but rarely collide on the *same* account
  at the *same* millisecond — contention is low, so paying the cost of a DB-level lock
  on every read is wasteful.
- Optimistic locking fails fast (throws `OptimisticLockException`) instead of blocking
  threads, which keeps the system responsive under load.
- It avoids deadlock risk entirely, since no row-level locks are held across the
  transaction boundary.

Trade-off: failed transfers due to version conflicts need to be retried by the caller
(or handled with a retry policy) — this is documented as a known limitation below.

### Why DTOs at the API boundary
Request/response DTOs are used instead of exposing JPA entities directly, to:
- Prevent leaking internal fields (e.g., `@Version`, audit columns) to API consumers
- Decouple the API contract from schema changes — the entity can evolve without
  breaking clients
- Allow validation annotations (`@NotNull`, `@Min`) to live on the DTO, separate from
  persistence concerns

### Why centralized exception handling (`@ControllerAdvice`)
Banking APIs need consistent, predictable error responses (status code + error code +
message) for downstream consumers and audit logs. A single `@ControllerAdvice` class
maps domain exceptions (e.g., `InsufficientFundsException`, `AccountNotFoundException`)
to structured HTTP responses, rather than scattering try/catch blocks across controllers.

### Known limitation / next iteration
Optimistic locking conflicts are currently surfaced as a generic error to the caller.
A production version would add automatic retry with backoff for transient conflicts,
and an idempotency key on the transfer endpoint to safely handle client retries.

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

* Kafka integration for async processing
* Docker support
* Unit & Integration Tests

---

## 👤 Author

Bincy Nizam
