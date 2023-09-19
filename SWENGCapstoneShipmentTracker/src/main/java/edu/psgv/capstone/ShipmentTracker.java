package edu.psgv.capstone;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class ShipmentTracker 
{
    public static void main(String[] args) throws CsvValidationException 
    {
        ShipmentTracker shipmentTracker = new ShipmentTracker();

        String filePath = "C:\\Users\\Raghavi Ramesh\\Desktop\\Penn State\\12) Capstone\\Excels\\ShipmentSummaryCSV.csv";

        try 
        {
            List<String> arrListCarrierServicesCSV = shipmentTracker.readCarrierServicesFromCSV(filePath);
            List<String> arrListTrackingNosCSV = shipmentTracker.readTrackingNumbersFromCSV(filePath);

            System.out.println("Carrier Services: " + arrListCarrierServicesCSV);
            System.out.println("Tracking Numbers: " + arrListTrackingNosCSV);
        } 
        catch (IOException e) 
        {
            System.out.println("ERROR - An error occurred while reading the CSV file: " + e.getMessage());
        }
    }

    public List<String> readCarrierServicesFromCSV(String filePath) throws IOException, CsvValidationException 
    {
        return readCSVColumn(filePath, "Carrier");
    }

    public List<String> readTrackingNumbersFromCSV(String filePath) throws IOException, CsvValidationException 
    {
        return readCSVColumn(filePath, "Tracking");
    }

    private List<String> readCSVColumn(String filePath, String columnName) throws IOException, CsvValidationException 
    {
        List<String> columnData = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) 
        {
            String[] header = reader.readNext();
            int columnIndex = Arrays.asList(header).indexOf(columnName);

            if (columnIndex == -1) 
            {
                throw new IllegalArgumentException("Column '" + columnName + "' not found in CSV.");
            }

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) 
            {
                columnData.add(nextLine[columnIndex]);
            }
        }

        return columnData;
    }
}
