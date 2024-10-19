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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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


    public static void createReadingsFromCsv(String filePath, KindOfMeter kindOfMeter) throws IOException, SQLException, ParseException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/main/resources/DbData.properties"));
        CSVParser csvParser = new CSVParser(filePath);
        Customer customer = null;
        String customerId = null;
        String meterId = null;
        DatabaseCon databaseCon = new DatabaseCon();
        databaseCon.openConnections(properties);
        ReadingDao readingDao = new ReadingDao(databaseCon.getConnection());
        CustomerDao customerDao = new CustomerDao(databaseCon.getConnection());

        while (csvParser.hasNext()) {
            String line = csvParser.next();
            String [] lineArray = line.split(";");
            LocalDate dateOfReading = LocalDate.of(0, 1, 1);
            double meterCount = 0.0;
            String comment = "";

            if (lineArray.length >= 2) {
                String dateString = lineArray[0];
                if (dateString.contains("\"")) {
                    dateString = dateString.replaceAll("\"", "");
                }

                if (lineArray[0].equals("\"Kunde\"")) {
                    customerId = lineArray[1].replaceAll("\"", "");
                } else if (lineArray[0].equals("\"Zählernummer\"")) {
                    meterId = lineArray[1];
                } else if (isNumeric(lineArray[1])) {


                    try {
                        dateOfReading = LocalDate.parse(
                                dateString,
                                DateTimeFormatter.ofPattern( "dd.MM.yyyy" )
                        );
                    } catch (DateTimeParseException e) {
                        //ignore
                    }


                    meterCount = DecimalFormat.getNumberInstance().parse(lineArray[1]).doubleValue();
                    if (lineArray.length > 2) {
                        comment = lineArray[2];
                    }


                }
                if(customerId != null) {
                    customer = customerDao.getCustomer(UUID.fromString(customerId));
                }

                if (!lineArray[0].equals("") && !lineArray[0].equals("\"Datum\"") && !lineArray[0].equals("\"Kunde\"") && !lineArray[0].equals("\"Zählernummer\"")) {
                    Reading reading = new Reading(UUID.randomUUID(), comment, customer, dateOfReading, kindOfMeter, meterCount, meterId, false);
                    readingDao.createReading(reading);
                }
            }


        }

    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
