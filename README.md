# 🗳️ Java eVoting System

A **Java-based eVoting application** with both **CLI and GUI interfaces**, powered by **MySQL** for persistent data storage. This system allows voters to securely log in, view elections, cast votes, and view results.

---

## 🚀 Features

* 🔐 **Secure Login System**
* 🗳️ **Vote Casting (One vote per election)**
* 📊 **Real-time Vote Counting**
* 🧾 **Election Results Display**
* 🖥️ **GUI Interface (Swing-based)**
* 💻 **CLI Interface (Console-based)**
* 🗃️ **MySQL Database Integration**
* 📜 **Login Activity Logging (IP + timestamp)**

---

## 🏗️ Project Structure

```
📁 eVoting-System
│
├── DBConnect.java        # Database connection handler
├── VoterDAO.java         # Voter operations (login, vote, register)
├── ElectionDAO.java      # Election & results handling
├── VotingAppGUI.java     # GUI interface (Swing)
└── Main.java             # CLI-based interface
```

---

## ⚙️ Technologies Used

* **Java (JDK 17+)**
* **Swing (GUI)**
* **JDBC**
* **MySQL**

---

## 🔌 Database Configuration

The system connects to MySQL using:

```java
private static final String JDBC_URL = "jdbc:mysql://localhost:3306/voting_system";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "your_password";
```

📌 Defined in: 

---

## 🧠 Core Functionalities

### 1. Voter Authentication

* Users log in using email & password
* Login attempts are logged with timestamp & IP

📌 Code: 

---

### 2. Election Management

* Fetch all elections
* Check if election is ongoing
* Retrieve candidates per election

📌 Code: 

---

### 3. Voting System

* Prevents multiple votes per election
* Stores vote in database
* Updates election results dynamically

📌 Code: 

---

### 4. GUI Interface

* User-friendly Swing UI
* Dynamic election & candidate loading
* Displays results after election ends

📌 Code: 

---

### 5. CLI Interface

* Simple console-based voting
* Step-by-step interaction

📌 Code: 

---

## 🗄️ Database Tables (Expected)

* `voters`
* `election`
* `candidate`
* `vote`
* `election_result`
* `Voter_Auth_Log`

---

## ▶️ How to Run

### 1. Setup Database

* Create database: `voting_system`
* Import required tables

---

### 2. Configure DB Credentials

Update in `DBConnect.java`:

```java
DB_USER = "root";
DB_PASSWORD = "your_password";
```

---

### 3. Compile & Run

#### ▶️ Run CLI version

```bash
javac *.java
java Main
```

#### ▶️ Run GUI version

```bash
java VotingAppGUI
```

---

## 🔒 Security Notes

* Passwords are stored in plain text ⚠️ (Improve with hashing like BCrypt)
* No input validation (can be enhanced)
* SQL handled via PreparedStatements (prevents SQL injection ✅)

---

## 📈 Possible Improvements

* 🔐 Password hashing (BCrypt)
* 🌐 Web version (Spring Boot)
* 📱 Mobile app integration
* 📊 Admin dashboard
* 🗳️ Blockchain-based voting (advanced)

---

## 👨‍💻 Author

**Soumadip**
Engineering Student

---

## ⭐ If you like this project

Give it a ⭐ on GitHub and feel free to contribute!

---

If you want, I can also:

* Make this **more ATS/project-report style**
* Add **ER diagram + schema SQL**
* Or create a **killer GitHub description + tags** 🚀
