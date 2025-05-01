# GlycoForge 🩸⚙️

**Zero-fluff insulin & meal management platform for diabetics.**  
Java 21 • Spring Boot 3 • MySQL 8 • Maven • Llama-3 chatbot

---

## 1  What it does
* Stores user profile (age, height, weight, insulin-to-carb ratio, sensitivity factor).  
* Accepts free-text meal entries, e.g.  
  `“Ate a banana, 300 g rice and 2 chicken breasts at 14:00”`.  
* Llama 3 parses the text → extracts macros → backend calculates  
  * **carb dose**  
  * **correction dose**  
  * **total insulin units**  
  * **ideal next injection time**  
* Persists meals & injections; shows history and latest recommendation.  
* Minimal three-page UI: **Register → Login → Dashboard (chat + live stats)**.

---

## 2  Tech stack

| Layer        | Tooling                                       |
|--------------|-----------------------------------------------|
| Runtime      | Java 21, Spring Boot 3.3, Lombok              |
| Persistence  | MySQL 8 (Docker), JPA/Hibernate, Flyway       |
| Build        | Maven 3.9+, Testcontainers, JUnit 5           |
| Front-end    | Thymeleaf + TailwindCSS                       |
| AI parsing   | `ghcr.io/ggerganov/llama.cpp` (LLM server)    |
| Auth         | JWT (15 min) + refresh cookie, BCrypt         |
| Dev ops      | Docker Compose, DBeaver for DB browsing       |

---

## 3  Project structure

glycoforge/ ├─ docker-compose.yml ├─ models/ # GGUF model goes here ├─ src/main/java/com/glycoforge │ ├─ GlycoforgeApplication.java │ ├─ config/ │ ├─ domain/ # User, Meal, Injection … │ ├─ service/ │ ├─ controller/ │ └─ dto/ ├─ src/main/resources/ │ ├─ application.yml │ └─ db/migration/ V1__init.sql └─ README.md

---

## 4  Quick start (localhost)

# 0) prerequisites: JDK 21, Maven 3.9+, Docker Desktop

```
git clone https://github.com/brandonhunt00/GlycoForge.git
cd GlycoForge
```


# 1) pull a Llama-3 model (example: 8B Q4) – fits in 8 GB RAM
```
mkdir -p models
aria2c -x16 -d models \
  https://huggingface.co/TheBloke/Llama-3-8B-Instruct-GGUF/resolve/main/llama-3-instruct-8B.Q4_K_M.gguf
```


# 2) containers: MySQL + llama.cpp
```
docker compose up -d db llama
docker compose ps          # wait for HEALTHY
```


# 3) build & boot Spring
```
mvn -q clean package -DskipTests
java -jar target/glycoforge-*.jar
```


Open:
http://localhost:8080/register – create an account
http://localhost:8080/login – sign in
Dashboard chat – log meals & get insulin recommendations


# 5) Connecting DBeaver (optional)

Field	Value
Host	localhost
Port	3306
DB	glycoforge
User	user
Pass	password


# 6) API cheat-sheet


Method	Endpoint	Purpose
POST	/api/auth/register	new user
POST	/api/auth/login	JWT login
GET	/api/user/me	current profile
PUT	/api/user/me	update profile
POST	/api/meal	log meal (free text)
POST	/api/injection/manual	manual insulin entry
GET	/api/recommendation/latest	latest dose suggestion
POST	/api/chat	proxy → llama.cpp
