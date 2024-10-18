package dev.hv;

import dev.hv.db.CustomerDao;
import dev.hv.db.DatabaseCon;
import dev.hv.db.ReadingDao;
import dev.hv.enums.Gender;
import dev.hv.enums.KindOfMeter;
import dev.hv.model.Customer;
import dev.hv.model.Reading;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.UUID;

public class ExampleDataCreator {



    public static void createCustomersFromCSV(String filePath) throws IOException, SQLException {
        CSVParser csvParser = new CSVParser(filePath);
        DatabaseCon databaseCon = new DatabaseCon();
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
        databaseCon.openConnections(properties);

        CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());

        while (csvParser.hasNext()) {
            String line = csvParser.next();
            String [] lineArray = line.split(",");
            if(!lineArray[0].equals("UUID")) {
                LocalDate parsedDate;

                Gender gender = switch (lineArray[1]) {
                        case "Frau" -> Gender.W;
                        case "Herr" -> Gender.M;
                        default -> Gender.U;
                    };

                if (lineArray.length == 5) {
                    parsedDate = LocalDate.parse(
                            lineArray[4] ,
                            DateTimeFormatter.ofPattern( "dd.MM.yyyy" )
                    );
                } else {
                    parsedDate = LocalDate.of(0, 1, 1);
                }
                Customer customer = new Customer(UUID.fromString(lineArray[0]), lineArray[2], lineArray[3], parsedDate, gender);
                customerDao.createCustomer(customer);

            }

        }

    }


    public static void createReadingsFromCsv(String filePath, ReadingDao readingDao, CustomerDao customerDao, KindOfMeter kindOfMeter) throws IOException, SQLException {
        CSVParser csvParser = new CSVParser(filePath);
        Customer customer = null;
        String customerId = null;
        String meterId = null;

        while (csvParser.hasNext()) {
            String line = csvParser.next();
            String [] lineArray = line.split(";");
            LocalDate dateOfReading = LocalDate.of(0, 1, 1);
            Double meterCount = 0.0;
            String comment = "";

            if (lineArray[0].equals("\"Kunde\"")) {
                customerId = lineArray[1].replaceAll("\"", "");
            } else if (lineArray[0].equals("\"Zählernummer\"")) {
                meterId = lineArray[1];
            } else if (!lineArray[0].equals("") && !lineArray[0].equals("Datum") && !lineArray[0].equals("\"Kunde\"")) {
                dateOfReading = LocalDate.parse(
                        lineArray[0],
                        DateTimeFormatter.ofPattern( "dd.MM.yyyy" )
                );
                meterCount = Double.valueOf(lineArray[1]);
                comment = lineArray[2];
            }
            System.out.println(customerId);
            if(customerId != null) {
                customer = customerDao.getCustomer(UUID.fromString(customerId));
            }


            if (!lineArray[0].equals("") && !lineArray[0].equals("\"Datum\"") && !lineArray[0].equals("\"Kunde\"")) {
                Reading reading = new Reading(UUID.randomUUID(), comment, customer, dateOfReading, kindOfMeter, meterCount, meterId, false);
                readingDao.createReading(reading);
            }
        }

    }

    public static void main(String[] args) throws IOException, SQLException {
        DatabaseCon databaseCon = new DatabaseCon();
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
        databaseCon.openConnections(properties);
        databaseCon.createAllTables();
        databaseCon.truncateAllTables();
        createCustomersFromCSV("src/main/resources/csv/kunden_utf8.csv");

        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());

        createReadingsFromCsv("src/main/resources/csv/heizung.csv", readingDao, customerDao, KindOfMeter.HEIZUNG);
        databaseCon.closeConnections();
    }
}
