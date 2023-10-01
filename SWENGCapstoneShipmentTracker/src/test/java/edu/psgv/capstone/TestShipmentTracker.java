package edu.psgv.capstone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TestShipmentTracker 
{
	 @Test
	 void testReadCSVFile() 
	 {
        ShipmentTrackerFrame frame = new ShipmentTrackerFrame();
        frame.Path = "C:\\Users\\Raghavi Ramesh\\Desktop\\Penn State\\12) Capstone\\Excels\\ShipmentSummaryCSV.csv";

        try 
        {
            frame.readCSVFile();
            assertNotNull(frame.arrListCarrierServicesCSV);
            assertFalse(frame.arrListCarrierServicesCSV.isEmpty());
            assertNotNull(frame.arrListTrackingNosCSV);
            assertFalse(frame.arrListTrackingNosCSV.isEmpty());
        } 
        catch (IOException | CsvValidationException e) 
        {
            fail("Exception thrown: " + e.getMessage());
        }
     }


    @Test
    void testRemoveDuplicatesCSV() 
    {
        ShipmentTrackerFrame frame = new ShipmentTrackerFrame();
        ArrayList<String> expectedCarrierServices = new ArrayList<String>();
        ArrayList<String> expectedTrackingNos = new ArrayList<String>();
        
        frame.Path = "C:\\Users\\Raghavi Ramesh\\Desktop\\Penn State\\12) Capstone\\Excels\\ShipmentSummaryCSV.csv";
        
        frame.arrListCarrierServicesCSV.add("Carrier1");
        frame.arrListCarrierServicesCSV.add("Carrier2");
        frame.arrListCarrierServicesCSV.add("Carrier2");
        frame.arrListTrackingNosCSV.add("123");
        frame.arrListTrackingNosCSV.add("456");
        frame.arrListTrackingNosCSV.add("123");
        
        frame.removeDuplicatesCSV();
        
        expectedCarrierServices.add("Carrier1");
        expectedCarrierServices.add("Carrier2");
        expectedTrackingNos.add("123");
        expectedTrackingNos.add("456");
        
        assertEquals(expectedCarrierServices, frame.arrListCarrierServicesInput);
        assertEquals(expectedTrackingNos, frame.arrListTrackingNosInput);
    }

    @Test
    void testReadMasterFile() 
    {
        ShipmentTrackerFrame frame = new ShipmentTrackerFrame();
        frame.strMasterFilePath = "C:\\TechdowShipmentTracking\\ShipmentSummaryMaster.xlsx";
        try 
        {
            frame.readMasterFile();
            // Assuming there are rows in the master file
            assertFalse(frame.arrListTrackingNosMaster.isEmpty());
            assertFalse(frame.arrListCarrierServicesMaster.isEmpty());
            assertFalse(frame.arrListDestStateMaster.isEmpty());
            assertFalse(frame.arrListDestCityMaster.isEmpty());
            assertFalse(frame.arrListDeliveryMaster.isEmpty());
            assertFalse(frame.arrListDeliveryStatMaster.isEmpty());
            assertFalse(frame.arrListTrackerDateMaster.isEmpty());
            assertFalse(frame.arrListDeliveryStartDtMaster.isEmpty());
            assertFalse(frame.arrListDeliveryEndDtMaster.isEmpty());
            assertFalse(frame.arrListTimeTakenMaster.isEmpty());
            assertFalse(frame.arrListWarningMaster.isEmpty());
        } catch (IOException e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    void testCompareAndPopulateLists() 
    {
        ShipmentTrackerFrame frame = new ShipmentTrackerFrame();
        ArrayList<String> expectedCarrierServices = new ArrayList<String>();
        ArrayList<String> expectedTrackingNos = new ArrayList<String>();
        
        frame.arrListTrackingNosInput.add("123");
        frame.arrListTrackingNosInput.add("456");
        frame.arrListCarrierServicesInput.add("Carrier1");
        frame.arrListCarrierServicesInput.add("Carrier2");
        frame.arrListTrackingNosMaster.add("123");
        frame.arrListDeliveryMaster.add("DELIVERED");

        frame.compareAndPopulateLists();
        
        expectedCarrierServices.add("Carrier2");
        expectedTrackingNos.add("456");
        
        assertEquals(expectedCarrierServices, frame.arrListCarrierServices);
        assertEquals(expectedTrackingNos, frame.arrListTrackingNos);
    }
}
