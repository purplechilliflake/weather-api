# Weather Info REST API

This is a backend assignment to provide weather information for a given Indian pincode and date. The application is built with Java and the Spring Boot framework, focusing on a clean, testable, and optimized design.

## Core Features

*   **RESTful Endpoint:** Provides a single `GET /api/weather` endpoint.
*   **Intelligent Caching:** Caches both pincode-to-coordinate lookups and final weather data in a database to minimize external API calls.
*   **Structured & Testable:** Built with a clean, layered architecture (`controller`, `service`, `repository`, `entity`) and includes a suite of unit tests for key components.
*   **Self-Documenting:** Uses Swagger/OpenAPI for interactive API documentation and testing.

---

## Technology Stack

*   **Language/Framework:** Java 17, Spring Boot 3
*   **Database:** H2 (In-memory, file-based for development) with Spring Data JPA
*   **Build Tool:** Apache Maven
*   **Testing:** JUnit 5, Mockito
*   **API Documentation:** SpringDoc (Swagger UI)

---

## How to Run the Application

**Prerequisites:**
*   JDK 17 or later
*   Apache Maven

**Steps:**

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/purplechilliflake/weather-api.git
    cd weather-api
    ```

2.  **Add your API Key:**
    Open the file `src/main/resources/application.properties` and replace the placeholder value for `openweathermap.api.key` with your own key from OpenWeatherMap.

3.  **Build the project and run tests:**
    ```bash
    ./mvnw clean package
    ```

4.  **Run the application:**
    ```bash
    java -jar target/weather-api-*.jar
    ```
    The application will start on `http://localhost:8080`.

---

## How to Use the API

Once the application is running, you can access the following resources:

### 1. Interactive API Documentation (Swagger UI)

The easiest way to test the API is via the built-in Swagger UI.

*   **URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Here you can see the endpoint, its parameters, and execute test calls directly from your browser.

### 2. Manual API Call (Example)

You can also call the API directly using tools like Postman or `curl`.

*   **Endpoint:** `GET /api/weather`
*   **Parameters:**
    *   `pincode` (String, e.g., `411014`)
    *   `date` (String, ISO format, e.g., `2024-10-15`)
*   **Example `curl` command:**
    ```bash
    curl -X GET "http://localhost:8080/api/weather?pincode=411014&date=2024-10-15"
    ```

### 3. H2 Database Console

To view the cached data directly in the database:

*   **URL:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
*   **JDBC URL:** `jdbc:h2:file:./weatherdb`
*   **Username:** `sa`
*   **Password:** `password`

---

## Design Choices & Testing Strategy

### Design
*   **API Caching:** The service layer first checks the database for existing data for a given `pincode` and `date`. If found, it returns the cached data immediately. If not, it fetches data from the external APIs, saves the result to the database, and then returns it. This optimizes subsequent requests for the same data.
*   **Historical Data Note:** The free tier of the OpenWeatherMap API only provides *current* weather. This application fetches the current weather and caches it against the requested date to fulfill the assignment's core caching requirement. A production version with a paid API key would call a specific historical/forecast endpoint based on the date.

### Testing
*   **Unit Tests:** The project includes comprehensive unit tests for the `WeatherService` and `WeatherController`.
    *   The `WeatherService` tests use **Mockito** to mock repository and `RestTemplate` dependencies, ensuring that the core caching and data-fetching logic is tested in isolation.
    *   The `WeatherController` tests use **`MockMvc`** to test the web layer, verifying correct request handling, parameter binding, and HTTP status code responses without needing a full server.
*   The full test suite can be run with the Maven command: `./mvnw test`.