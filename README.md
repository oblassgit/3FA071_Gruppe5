# 3FA071_Gruppe5

[![maven-test](https://github.com/oblassgit/3FA071_Gruppe5/actions/workflows/maven-test.yml/badge.svg)](https://github.com/oblassgit/3FA071_Gruppe5/actions/workflows/maven-test.yml)

https://github.com/oblassgit/3FA071_Gruppe5_UI
## guide to get started

## 🔧 1. Softwareaufbau

Die Anwendung besteht aus drei klar getrennten Hauptkomponenten:

### 🖥️ Frontend: Vue.js

Das Frontend basiert auf dem JavaScript-Framework **Vue.js** und stellt die Benutzeroberfläche der Anwendung bereit. Die Kommunikation mit dem Backend erfolgt über asynchrone HTTP-Requests (REST API) mithilfe von `axios`.

- **Framework:** Vue.js 3
- **Sprache:** JavaScript (ES6+)
- **Build-Tool:** Vite oder Vue CLI
- **API-Kommunikation:** REST über HTTP (Axios)
- **Zielplattform:** Webbrowser (responsive)

### 🖧 Backend: Java (Jersey + Maven)

Das Backend ist mit **Java 21** realisiert und verwendet **Jersey (JAX-RS)** zur Implementierung von REST-konformen Webservices. Es ist modular aufgebaut mit klarer Trennung zwischen API-Schicht, Businesslogik und Datenzugriff.

- **Sprache:** Java 21
- **Frameworks:** Jersey (JAX-RS), Jackson für JSON/XML
- **Build-System:** Maven
- **Tests:** JUnit 5, Mockito
- **Code-Coverage:** JaCoCo
- **Deployment:** Als eigenständige ausführbare JAR (via `exec-maven-plugin`)
- **Dateiformate:** JSON (primär), optional XML/CSV

### 🗄️ Datenbank: MariaDB

Die persistenten Daten werden in einer **MariaDB**-SQL-Datenbank gespeichert. Die Verbindung erfolgt über JDBC mithilfe des offiziellen MariaDB Java-Clients.

- **Datenbanktyp:** Relationale SQL-Datenbank (MariaDB)
- **Zugriff:** JDBC (MariaDB Java Client)
- **Schema-Verwaltung:** manuell oder via SQL-Skripte
- **Authentifizierung:** Benutzername + Passwort

### 🔗 API-Kommunikation

Das Frontend und Backend kommunizieren über eine **RESTful API**. Die Endpunkte unterstützen die folgenden HTTP-Methoden:

- `GET` – Daten lesen
- `POST` – Neue Daten erstellen
- `PUT` / `PATCH` – Daten aktualisieren
- `DELETE` – Daten löschen

Standardmäßig erfolgt die Datenübertragung im **JSON-Format**, alternativ sind **XML** oder **CSV** möglich (via Content Negotiation).

## UML Sequenzdiagramm:

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

## UML Klassendiagramm:
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
