# 3FA071_Gruppe5

[![maven-test](https://github.com/oblassgit/3FA071_Gruppe5/actions/workflows/maven-test.yml/badge.svg)](https://github.com/oblassgit/3FA071_Gruppe5/actions/workflows/maven-test.yml)

[Link zur Benutzeroberfläche](https://github.com/oblassgit/3FA071_Gruppe5_UI)

## ✅ Testabdeckung

[███████████████████████████████████████████████████░░░░░░░░░░░░░] 82%

___

## 👌 How to get it Running!

- Erster Step : Führe diesen Befehlt aus um das Backend zu starten -> "mvn clean compile exec:java".
- Zweiter Step : Installiere NPM mit dem Befehlt  -> "npm install". 
- Dritter Step : Führe diesen Befehl aus um das Backend zu starten -> "npm run preview".


## 🔧 Softwarearchitektur

Die Anwendung ist dreischichtig aufgebaut:

### 🖧 Backend – Java (Jersey)

- Sprache: **Java 21**
- REST-API mit **JAX-RS (Jersey 3.x)**
- Struktur: Ressourcen- / Service- / DAO-Schicht
- JSON/XML/CSV-Serialisierung via **Jackson**
- Build-Tool: **Maven**
- Tests: **JUnit 5**, **Mockito**
- Code-Coverage: **JaCoCo**

### 🖥️ Frontend – Vue.js

- Framework: **Vue.js 3** (Composition API)
- Sprache: **JavaScript (ES6+)**
- API-Kommunikation über **Axios** via REST (HTTP)
- Build-Tool: **Vite** oder **Vue CLI**
- Datenformat: **JSON**

### 🗄️ Datenbank – MariaDB

- Typ: **Relationale SQL-Datenbank**
- Engine: **MariaDB** (Version ≥ 10.x)
- JDBC-Treiber: `mariadb-java-client` (v3.3.3)
- Zugriff über DAO mit JDBC
- Konfiguration via `.env` / `application.properties`

### 🔗 API-Spezifikation

- Architektur: **RESTful API**
- Methoden: `GET`, `POST`, `PUT`, `DELETE`
- Standardformat: **JSON**, optional: XML / CSV

___

## 📡 REST API Endpunkte

### Kunden-API – `/customers`

- **GET `/customers`**: Liste von Kunden, optional mit Filtern (z.B. `start`, `end`, `gender`)
- **GET `/customers/{uuid}`**: Einzelner Kunde per UUID
- **DELETE `/customers/{uuid}`**: Löscht einen Kunden
- **POST `/customers`**: Erstellt einen neuen Kunden
- **PUT `/customers`**: Aktualisiert einen bestehenden Kunden
- **GET `/customers/export/csv`**: CSV-Export aller Kunden
- **GET `/customers/export/xml`**: XML-Export aller Kunden

### Readings-API – `/readings`

- **GET `/readings`**: Filter: `customer`, `start`, `end`, `kindOfMeter`
- **GET `/readings/{uuid}`**: Einzelner Reading-Eintrag
- **DELETE `/readings/{uuid}`**: Löscht einen Reading-Eintrag
- **POST `/readings`**: Legt einen neuen Reading-Eintrag an
- **PUT `/readings`**: Aktualisiert einen bestehenden Reading-Eintrag
- **GET `/readings/export/xml`**: Exportiert alle Readings als `.xml`
- **GET `/readings/export/csv`**: Exportiert alle Readings als `.csv`

## UML Sequenzdiagramm für Beispiel GetCustomer 
```
       ┌─┐                                                                                                                                                              ,.-^^-._      
       ║"│                                                                                                                                                             |-.____.-|     
       └┬┘                                                                                                                                                             |        |     
       ┌┼┐                                                                             ┌──────────────────┐                                                            |        |     
        │                                   ┌───────────────┐                          │Jersey Resource   │                          ┌───────────┐                     |        |     
       ┌┴┐                                  │Vue.js Frontend│                          │(CustomerResource)│                          │CustomerDao│                     '-.____.-'     
      User                                  └───────┬───────┘                          └─────────┬────────┘                          └─────┬─────┘                     Database       
        │                                           │                                            │                                         │                               │          
        │                                           │                            ╔═══════════════╧═╗                                       │                               │          
════════╪═══════════════════════════════════════════╪════════════════════════════╣ Filter auslösen ╠═══════════════════════════════════════╪═══════════════════════════════╪══════════
        │                                           │                            ╚═══════════════╤═╝                                       │                               │          
        │                                           │                                            │                                         │                               │          
        │Select filter (startDate, endDate, gender) │                                            │                                         │                               │          
        │──────────────────────────────────────────>│                                            │                                         │                               │          
        │                                           │                                            │                                         │                               │          
        │                                           │────┐                                       │                                         │                               │          
        │                                           │    │ build query params                    │                                         │                               │          
        │                                           │<───┘                                       │                                         │                               │          
        │                                           │                                            │                                         │                               │          
        │                                           │GET /customers?start=...&end=...&gender=... │                                         │                               │          
        │                                           │───────────────────────────────────────────>│                                         │                               │          
        │                                           │                                            │                                         │                               │          
        │                                           │                                            │getCustomers(startDate, endDate, gender) │                               │          
        │                                           │                                            │────────────────────────────────────────>│                               │          
        │                                           │                                            │                                         │                               │          
        │                                           │                                            │                                         │  SQL SELECT * FROM customer   │          
        │                                           │                                            │                                         │  WHERE ... (with filters)     │          
        │                                           │                                            │                                         │──────────────────────────────>│          
        │                                           │                                            │                                         │                               │          
        │                                           │                                            │                                         │ResultSet (filtered customers) │          
        │                                           │                                            │                                         │<─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ │          
        │                                           │                                            │                                         │                               │          
        │                                           │                                            │             List<Customer>              │                               │          
        │                                           │                                            │<────────────────────────────────────────│                               │          
        │                                           │                                            │                                         │                               │          
        │                                           │             HTTP 200 OK                    │                                         │                               │          
        │                                           │             JSON CustomerList              │                                         │                               │          
        │                                           │<───────────────────────────────────────────│                                         │                               │          
        │                                           │                                            │                                         │                               │          
        │                                           │────┐                                       │                                         │                               │          
        │                                           │    │ Render customer list                  │                                         │                               │          
        │                                           │<───┘                                       │                                         │                               │          
      User                                  ┌───────┴───────┐                          ┌─────────┴────────┐                          ┌─────┴─────┐                     Database       
       ┌─┐                                  │Vue.js Frontend│                          │Jersey Resource   │                          │CustomerDao│                      ,.-^^-._      
       ║"│                                  └───────────────┘                          │(CustomerResource)│                          └───────────┘                     |-.____.-|     
       └┬┘                                                                             └──────────────────┘                                                            |        |     
       ┌┼┐                                                                                                                                                             |        |     
        │                                                                                                                                                              |        |     
       ┌┴┐                                                                                                                                                             '-.____.-'
```

___

## UML Relationmodell:
```
               ┌──────────────────────────────────┐               
               │customer                          │               
               ├──────────────────────────────────┤               
               │* id : UUID                       │               
               │--                                │               
               │first_name : VARCHAR(100)         │               
               │last_name  : VARCHAR(100)         │               
               │birth_date : DATE                 │               
               │gender     : ENUM('D','M','U','W')│               
               └──────────────────────────────────┘               
                                 |                                
                                 |                                
┌────────────────────────────────────────────────────────────────┐
│reading                                                         │
├────────────────────────────────────────────────────────────────┤
│* id              : UUID                                        │
│--                                                              │
│customer_id       : UUID                                        │
│comment           : VARCHAR(1000)                               │
│date_of_reading   : DATE                                        │
│meter_count       : INT                                         │
│meter_id          : VARCHAR(100)                                │
│kind_of_meter     : ENUM('HEIZUNG','STROM','WASSER','UNBEKANNT')│
│substitute        : BOOL                                        │
└────────────────────────────────────────────────────────────────┘
```

___

## CI/CD Pipeline: Maven-Test

Automatisiert Tests und Coverage-Analyse bei jedem Push/Pull Request auf `main`.

### Schritte:

1. **Repository Checkout**: Lädt den Code.
2. **MariaDB Setup**: Startet eine MariaDB-Instanz mit definierten Secrets.
3. **Verbindungsprüfung**: Testet die Datenbankverbindung.
4. **JDK 21 Setup**: Installiert Java 21.
5. **Datenbankkonfiguration**: Schreibt Verbindungsdaten in `DbData.properties`.
6. **Build und Tests**: Führt `mvn site -P test` aus.
7. **Coverage-Report**: Zeigt Coverage-Details im PR an (min. 50 % gesamt, 80 % geänderte Dateien).
