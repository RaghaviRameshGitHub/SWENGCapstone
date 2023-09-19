package edu.psgv.capstone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.List;

class TestShipmentTracker {
    private ShipmentTracker shipmentTracker;
    private final String testCsvFilePath = "C:\\Users\\Raghavi Ramesh\\Desktop\\Penn State\\12) Capstone\\Excels\\ShipmentSummaryCSV.csv"; // Replace with your test CSV file path

    @BeforeEach
    public void setUp() {
        shipmentTracker = new ShipmentTracker();
    }

    @Test
    public void testReadCarrierServicesFromCSV() throws IOException, CsvValidationException {
        List<String> carrierServices = shipmentTracker.readCarrierServicesFromCSV(testCsvFilePath);

        assertNotNull(carrierServices);
        assertFalse(carrierServices.isEmpty());
        // Add more specific assertions based on your test CSV content
    }

    @Test
    public void testReadTrackingNumbersFromCSV() throws IOException, CsvValidationException {
        List<String> trackingNumbers = shipmentTracker.readTrackingNumbersFromCSV(testCsvFilePath);

        assertNotNull(trackingNumbers);
        assertFalse(trackingNumbers.isEmpty());
        // Add more specific assertions based on your test CSV content
    }
}
