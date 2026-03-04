# 🪙 Starla Jewels API Automation Framework

> **Author:** Abhinandan Basu  
> **Tech Stack:** Java · Maven · TestNG · REST Assured · ExtentReports  
> **Type:** REST API Test Automation Framework (OOP-based)

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Project Structure](#project-structure)
3. [OOP Concepts Used](#oop-concepts-used)
4. [Prerequisites & Setup](#prerequisites--setup)
5. [Environment Variables / Configuration](#environment-variables--configuration)
6. [How to Run Tests](#how-to-run-tests)
7. [Running Tests on Jenkins (Port 8080)](#running-tests-on-jenkins-port-8080)
8. [Test Reports](#test-reports)

---

## 📌 Project Overview

This framework automates REST API testing for the **Starla Jewels** inventory management system. It covers:

- **Login API** – Positive, negative, SQL injection, and precondition validation
- **Inventory API** – Positive create, duplicate detection, and rollback scenario

---

## 🗂️ Project Structure

```
STARLA_JEWELS_API_AUTOMATION/
├── pom.xml                         # Maven build + Surefire plugin configuration
├── testng.xml                      # TestNG suite definition
├── reports/
│   └── StarlaJewelsReport.html     # Generated ExtentReport (auto-created after run)
└── src/
    ├── main/
    │   ├── java/org/Abhinandan_Basu_Project/
    │   │   ├── Config/
    │   │   │   ├── configManager.java          # Loads config.properties (Singleton)
    │   │   │   └── RequestSpecFactory.java     # Builds REST-Assured RequestSpec (Factory)
    │   │   ├── POJOS/
    │   │   │   ├── UserCredentials.java        # Login payload POJO (Encapsulation)
    │   │   │   └── InventoryRequest.java       # Inventory payload POJO
    │   │   ├── auth/
    │   │   │   └── AuthTokenProvider.java      # Thread-safe token store (Singleton)
    │   │   ├── builder/
    │   │   │   └── InventoryRequestBuilder.java # Creates InventoryRequest (Builder)
    │   │   ├── client/
    │   │   │   ├── AuthClient.java             # Wraps login API calls
    │   │   │   └── InventoryClient.java        # Wraps inventory API calls (Overloading)
    │   │   └── utils/
    │   │       ├── DataGenerator.java          # Generates unique vendor codes (Utility)
    │   │       ├── ExtentManager.java          # Singleton report manager
    │   │       └── TestListeners.java          # TestNG listener for ExtentReports
    │   └── resources/
    │       └── config/
    │           └── config.properties           # All environment/test data config
    └── test/
        └── java/org/Abhinandan_Basu_Project/tests/
            ├── BaseTest.java       # @BeforeSuite login setup (Inheritance base)
            ├── LoginTests.java     # Login test cases (extends BaseTest)
            └── InventoryTests.java # Inventory test cases (extends BaseTest)
```

---

## 🧱 OOP Concepts Used

This framework is built around solid Object-Oriented Programming principles. Below is a detailed explanation of each concept applied in the codebase.

---

### 1. 🔒 Encapsulation

**Where:** `UserCredentials.java`

Encapsulation means hiding internal data and exposing it only through public methods (getters/setters).

```java
// src/main/java/.../POJOS/UserCredentials.java
public class UserCredentials {
    // Private-style fields with explicit getter/setter control
    public String user_email;
    public String password;

    public String getUser_email() { return user_email; }
    public void setUser_email(String user_email) { this.user_email = user_email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserCredentials(String user_email, String password) {
        this.user_email = user_email;
        this.password = password;
    }
}
```

**Why it matters:** Prevents direct field mutation from outside — the object controls how its state is changed.

---

### 2. 🧬 Inheritance

**Where:** `LoginTests.java`, `InventoryTests.java` → extend `BaseTest.java`

Inheritance allows child classes to reuse parent class behavior.

```java
// BaseTest.java — parent class
public class BaseTest {
    @BeforeSuite
    public void setup() {
        Response res = AuthClient.login();
        AuthTokenProvider.setToken(res.jsonPath().getString("token"));
        AuthTokenProvider.setUserId(res.jsonPath().getString("userId"));
    }
}

// LoginTests.java — child class inherits @BeforeSuite from BaseTest
public class LoginTests extends BaseTest {
    @Test
    public void positiveLogin() { ... }
}

// InventoryTests.java — also inherits setup()
public class InventoryTests extends BaseTest {
    @Test
    public void positiveInventory() { ... }
}
```

**Why it matters:** Login/authentication setup runs once before all tests — both `LoginTests` and `InventoryTests` automatically inherit this behavior without code duplication.

---

### 3. 🎭 Polymorphism (Method Overloading)

**Where:** `AuthClient.java` and `InventoryClient.java`

Overloading is compile-time polymorphism — same method name, different parameters.

```java
// AuthClient.java
public class AuthClient {
    // Default login (reads from config.properties)
    public static Response login() { ... }

    // Parameterized login (for negative/SQL injection tests)
    public static Response login(String email, String password) { ... }
}

// InventoryClient.java
public class InventoryClient {
    // Uses auto-generated vendor code
    public Response createInventory() { ... }

    // Uses a specific (fixed) vendor code — used for duplicate testing
    public Response createInventory(String code) { ... }
}
```

**Why it matters:** Keeps the API clean. Tests can call the same method name with different data — no need for separate `loginWithParams()` or `createInventoryWithCode()` methods.

---

### 4. 🏭 Factory Pattern (Creational Design Pattern)

**Where:** `RequestSpecFactory.java`

A Factory exposes a static method to create a configured object, hiding construction details.

```java
// RequestSpecFactory.java
public class RequestSpecFactory {
    private RequestSpecFactory() {} // Cannot be instantiated

    public static RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(configManager.getProperty("BASE_URL"))
                .addHeader("Authorization", "Bearer" + AuthTokenProvider.getToken())
                .addHeader("x-user-id", AuthTokenProvider.getUserId())
                .addHeader("x-organisation-id", configManager.getProperty("organisationid"))
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
```

**Why it matters:** All REST-Assured request specs are consistently built in one place — adding a header or changing the base URI only requires editing one file.

---

### 5. 🔁 Singleton Pattern (Creational Design Pattern)

**Where:** `configManager.java` and `ExtentManager.java`

A Singleton ensures only one instance of an object exists throughout the application lifecycle.

```java
// configManager.java — Properties loaded once via static block
public class configManager {
    private static final Properties properties = new Properties();
    static {
        // Loaded once when class is first used
        InputStream input = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("config/config.properties");
        properties.load(input);
    }
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

// ExtentManager.java — Report instance created only once
public class ExtentManager {
    private static ExtentReports extent;
    public static ExtentReports getInstance() {
        if (extent == null) {
            extent = new ExtentReports();
            extent.attachReporter(new ExtentSparkReporter("reports/StarlaJewelsReport.html"));
        }
        return extent;
    }
}
```

**Why it matters:** Configuration is loaded from disk only once — no repeated I/O. The report file is never overwritten mid-run.

---

### 6. 🏗️ Builder Pattern

**Where:** `InventoryRequestBuilder.java`

The Builder assembles complex objects step by step from config, keeping test code clean.

```java
// InventoryRequestBuilder.java
public class InventoryRequestBuilder {
    public static InventoryRequest build() {
        InventoryRequest req = new InventoryRequest();
        req.item_name = configManager.getProperty("item_name");
        req.vendor_jewel_code = DataGenerator.vendorCode(); // Auto-generated
        req.gross_weight = Double.parseDouble(configManager.getProperty("gross_weight"));
        // ... more fields
        return req;
    }

    // Overloaded: fixed code for duplicate/rollback tests
    public static InventoryRequest build(String code) {
        InventoryRequest req = build();
        req.vendor_jewel_code = code;
        return req;
    }
}
```

**Why it matters:** Test methods call `InventoryRequestBuilder.build()` — they don't need to know every field of `InventoryRequest`. Changes to the payload structure only require editing the Builder.

---

### 7. 🎧 Interface Implementation (Observer Pattern)

**Where:** `TestListeners.java` implements `ITestListener`

Implementing a TestNG interface hooks into the test lifecycle.

```java
public class TestListeners implements ITestListener {
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        test.set(extent.createTest(result.getMethod().getMethodName()));
    }
    @Override
    public void onTestSuccess(ITestResult result) { test.get().pass("Test Passed"); }
    @Override
    public void onTestFailure(ITestResult result) { test.get().fail(result.getThrowable()); }
    @Override
    public void onFinish(ITestContext context) { extent.flush(); }
}
```

**Why it matters:** Separates reporting logic completely from test logic. Tests don't need to call `extent.flush()` — the listener handles the full lifecycle.

---

### 8. 🛡️ Static Utility Classes (Abstraction)

**Where:** `DataGenerator.java`, `AuthTokenProvider.java`

These are utility classes with private constructors that cannot be instantiated.

```java
// DataGenerator.java
public class DataGenerator {
    private DataGenerator() {} // Prevent instantiation

    public static String vendorCode() {
        return "VD-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

// AuthTokenProvider.java
public class AuthTokenProvider {
    private static String token;
    private static String userId;
    private AuthTokenProvider() {} // Prevent instantiation

    public static String getToken() { return token; }
    public static void setToken(String token) { AuthTokenProvider.token = token; }
}
```

**Why it matters:** Utility functions are grouped logically, stateless, and accessible anywhere without creating objects.

---

## ⚙️ Prerequisites & Setup

### 1. Software Requirements

| Tool        | Version Required    | Download                                          |
|-------------|---------------------|---------------------------------------------------|
| Java (JDK)  | 11 or higher        | https://adoptium.net/                             |
| Maven       | 3.6+                | https://maven.apache.org/download.cgi             |
| Git         | Any                 | https://git-scm.com/                              |
| IntelliJ IDEA | Any (optional)   | https://www.jetbrains.com/idea/                   |

### 2. Clone the Repository

```bash
git clone <your-repository-url>
cd STARLA_JEWELS_API_AUTOMATION
```

### 3. Verify Java and Maven Installation

```bash
java -version
mvn -version
```

### 4. Install Dependencies

Maven automatically downloads all dependencies on the first build:

```bash
mvn clean install -DskipTests
```

---

## 🔑 Environment Variables / Configuration

All test configuration is managed via:

```
src/main/resources/config/config.properties
```

### Configuration Properties

| Property            | Description                                  | Example Value                              |
|---------------------|----------------------------------------------|--------------------------------------------|
| `BASE_URL`          | Base URL of the Starla Jewels API            | `https://api-test.starlajewels.com/api`    |
| `valid_email`       | Valid login email for positive tests         | `test@test.com`                            |
| `valid_password`    | Valid login password for positive tests      | `test1234`                                 |
| `invalid_email`     | Invalid email for negative tests             | `wrong@test.com`                           |
| `invalid_password`  | Invalid password for negative tests          | `wrong123`                                 |
| `sql_injection`     | SQL injection payload for security tests     | `' OR 1=1 --`                              |
| `categoryId`        | Inventory category ID                        | `2`                                        |
| `vendorId`          | Vendor ID for inventory creation             | `3`                                        |
| `stockStatusId`     | Stock status ID                              | `12`                                       |
| `organisationId`    | Organisation ID sent in API headers          | `1`                                        |
| `item_name`         | Default inventory item name                  | `Gold Ring`                                |
| `item_description`  | Default item description                     | `22K Wedding Ring`                         |
| `vendor_design_code`| Fixed vendor design code                    | `VD-1001`                                  |
| `gross_weight`      | Gross weight of the item                     | `10.5`                                     |
| `cost`              | Cost of the item (in INR)                    | `55000`                                    |
| `stock_owner_type`  | Who owns the stock                           | `VENDOR`                                   |

> ⚠️ **Never commit real credentials to version control.** For CI/CD pipelines, override sensitive properties using Jenkins Parameters (see Jenkins section below) or system environment variables.

---

## ▶️ How to Run Tests

### Option 1: Run All Tests via Maven (CMD / Terminal)

This is the recommended way to run tests. The `maven-surefire-plugin` (v3.5.5) configured in `pom.xml` automatically picks up `testng.xml`:

```bash
# Navigate to the project root
cd "C:\Users\abasu1\OneDrive - Infor\Documents\STARLA_JEWELS_ASSIGNMENT\STARLA_JEWELS_API_AUTOMATION"

# Clean previous build artifacts and run all tests
mvn clean test
```

### Option 2: Run with Verbose Output

```bash
mvn clean test -e -X
```

### Option 3: Run a Specific Test Class

```bash
# Run only Login tests
mvn clean test -Dtest=LoginTests

# Run only Inventory tests
mvn clean test -Dtest=InventoryTests
```

### Option 4: Run Using a Specific TestNG Suite File

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### How the Surefire Plugin is Configured

The `pom.xml` binds TestNG suite execution directly:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.5</version>
    <configuration>
        <suiteXmlFiles>
            <suiteXmlFile>testng.xml</suiteXmlFile>
        </suiteXmlFiles>
    </configuration>
</plugin>
```

This means `mvn clean test` will:
1. Compile all source files
2. Load `testng.xml`
3. Run `LoginTests` → 4 test methods
4. Run `InventoryTests` → 3 test methods
5. Generate the ExtentReport at `reports/StarlaJewelsReport.html`

---

## 🔧 Running Tests on Jenkins (Port 8080)

These are the exact steps followed to configure and run the framework on a **Jenkins server running on `http://localhost:8080`**.

---

### Step 1 — Start Jenkins

Open CMD and start Jenkins (if running as a WAR):

```bash
java -jar jenkins.war --httpPort=8080
```

Or if installed as a service on Windows, start via:

```
Services → Jenkins → Start
```

Access Jenkins at: **http://localhost:8080**

---

### Step 2 — Install Required Jenkins Plugins

Go to `Manage Jenkins → Manage Plugins → Available` and install:

- ✅ **Maven Integration Plugin**
- ✅ **TestNG Results Plugin** (for test result visualization)
- ✅ **Git Plugin**
- ✅ **Parameterized Trigger Plugin** (for parameterized builds)

---

### Step 3 — Configure Maven in Jenkins

1. Go to `Manage Jenkins → Global Tool Configuration`
2. Under **Maven** → Click **Add Maven**
3. Name: `Maven 3.9` (or your installed version)
4. Check **Install automatically** OR provide the Maven home path (e.g., `C:\Program Files\Maven\apache-maven-3.9.x`)
5. Click **Save**

---

### Step 4 — Create a New Jenkins Job (Parameterized)

1. Click **New Item** → Enter job name: `StarlaJewels-API-Tests`
2. Select **Freestyle Project** (or **Maven Project**) → Click **OK**

---

### Step 5 — Enable Parameterized Build

1. Check ✅ **This project is parameterized**
2. Click **Add Parameter** → Select **String Parameter** for each:

| Parameter Name    | Default Value                              | Description                        |
|-------------------|--------------------------------------------|------------------------------------|
| `BASE_URL`        | `https://api-test.starlajewels.com/api`   | API Base URL                       |
| `valid_email`     | `test@test.com`                            | Valid login email                  |
| `valid_password`  | `test1234`                                 | Valid login password               |
| `invalid_email`   | `wrong@test.com`                           | Invalid email for negative tests   |
| `invalid_password`| `wrong123`                                 | Invalid password                   |
| `categoryId`      | `2`                                        | Inventory category ID              |
| `vendorId`        | `3`                                        | Vendor ID                          |
| `organisationId`  | `1`                                        | Organisation ID                    |

---

### Step 6 — Configure Source Code Management (SCM)

1. Under **Source Code Management** → Select **Git**
2. Enter your repository URL
3. Add credentials if required

---

### Step 7 — Add Build Step

#### For a **Freestyle Project**:

1. Under **Build** → Click **Add build step** → Select **Invoke top-level Maven targets**
2. Maven version: `Maven 3.9`
3. Goals:
```
clean test
-DBASE_URL=${BASE_URL}
-Dvalid_email=${valid_email}
-Dvalid_password=${valid_password}
-Dinvalid_email=${invalid_email}
-Dinvalid_password=${invalid_password}
-DcategoryId=${categoryId}
-DvendorId=${vendorId}
-DorganisationId=${organisationId}
```

> 💡 Jenkins parameters prefixed with `-D` are passed as JVM system properties to Maven. However, **this framework reads from `config.properties`** — not system properties. To override config properties dynamically from Jenkins, use the approach below.

#### Passing Jenkins Parameters into `config.properties` 

Add a **Pre-Build Shell/Batch step** that writes a `config.properties` override before Maven runs:

**Windows Batch Command:**
```batch
echo BASE_URL=%BASE_URL% > src\main\resources\config\config.properties
echo valid_email=%valid_email% >> src\main\resources\config\config.properties
echo valid_password=%valid_password% >> src\main\resources\config\config.properties
echo invalid_email=%invalid_email% >> src\main\resources\config\config.properties
echo invalid_password=%invalid_password% >> src\main\resources\config\config.properties
echo categoryId=%categoryId% >> src\main\resources\config\config.properties
echo vendorId=%vendorId% >> src\main\resources\config\config.properties
echo organisationId=%organisationId% >> src\main\resources\config\config.properties
echo stockStatusId=12 >> src\main\resources\config\config.properties
echo item_name=Gold Ring >> src\main\resources\config\config.properties
echo item_description=22K Wedding Ring >> src\main\resources\config\config.properties
echo vendor_design_code=VD-1001 >> src\main\resources\config\config.properties
echo gross_weight=10.5 >> src\main\resources\config\config.properties
echo cost=55000 >> src\main\resources\config\config.properties
echo stock_owner_type=VENDOR >> src\main\resources\config\config.properties
echo sql_injection=' OR 1=1 -- >> src\main\resources\config\config.properties
```

Then add the Maven build step:
```
clean test
```

---

### Step 8 — Add Post-Build Actions

1. **Publish TestNG Results:**
   - Click **Add post-build action** → **Publish TestNG Results**
   - TestNG XML report pattern: `target/surefire-reports/testng-results.xml`

2. **Archive Artifacts (HTML Report):**
   - Click **Add post-build action** → **Archive the artifacts**
   - Files to archive: `reports/StarlaJewelsReport.html`

---

### Step 9 — Configure Cron (Scheduled Build Trigger)

1. Under **Build Triggers** → Check ✅ **Build periodically**
2. Schedule field (cron syntax):

| Cron Expression      | Meaning                              |
|----------------------|--------------------------------------|
| `H 8 * * 1-5`        | Every weekday at 8:00 AM             |
| `H/30 * * * *`       | Every 30 minutes                     |
| `0 0 * * *`          | Every day at midnight                |
| `H 9 * * 1`          | Every Monday at 9:00 AM              |
| `H 8,20 * * *`       | Twice daily — 8 AM and 8 PM          |

**Example: Run every weekday at 8:00 AM**

```
H 8 * * 1-5
```

> 📌 Use `H` (hash) instead of a fixed minute — Jenkins distributes builds across the time window to reduce load spikes.

---

### Step 10 — Run the Build

- **Manual run:** Click **Build with Parameters** → Review/edit parameter values → Click **Build**
- **Scheduled run:** Jenkins will automatically trigger according to the cron expression

Monitor progress via **Console Output** in real time.

---

## 📊 Test Reports

After a test run, the **ExtentReports** HTML report is generated at:

```
reports/StarlaJewelsReport.html
```

Open it in any browser to see:
- ✅ Passed tests (green)
- ❌ Failed tests (red) with full stack trace
- ⏭️ Skipped tests (yellow)
- Response time per test

---

## 📦 Dependencies Summary

| Dependency             | Version | Purpose                                  |
|------------------------|---------|------------------------------------------|
| REST Assured           | 6.0.0   | HTTP client for REST API testing         |
| TestNG                 | 7.12.0  | Test execution framework                 |
| Jackson Databind       | 2.21.1  | Java object ↔ JSON serialization         |
| ExtentReports          | 5.1.2   | HTML test reporting                      |
| Maven Surefire Plugin  | 3.5.5   | Runs TestNG suites via `mvn test`        |

---

## 📬 Contact

**Author:** Abhinandan Basu  
**Project:** Starla Jewels API Automation Framework
