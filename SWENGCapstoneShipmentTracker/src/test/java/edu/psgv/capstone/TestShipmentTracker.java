package edu.psgv.capstone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.text.ParseException;
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
    
    @Test
    void testTrackingUPS() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
    {
        ShipmentTrackerFrame frame = new ShipmentTrackerFrame();
        
        ArrayList<String> expectedTrackingService = new ArrayList<String>();
    	ArrayList<String> expectedTrackingNos = new ArrayList<String>();
    	ArrayList<String> expectedDestinationState = new ArrayList<String>();
    	ArrayList<String> expectedDestinationCity = new ArrayList<String>();
    	ArrayList<String> expectedDelivery = new ArrayList<String>();
    	ArrayList<String> expectedDeliveryStatus = new ArrayList<String>();
    	ArrayList<String> expectedDeliveryStartDate = new ArrayList<String>();
    	ArrayList<String> expectedDeliveryEndDate = new ArrayList<String>();
    	ArrayList<String> expectedWarning = new ArrayList<String>();
    	ArrayList<String> expectedTimeTaken = new ArrayList<String>();
    	ArrayList<String> expectedTrackerDate = new ArrayList<String>();

        frame.trackingUPS("1ZY5F5030332143355");
        
        expectedTrackingService.add("UPS");
        expectedTrackingNos.add("1ZY5F5030332143355");
        expectedDestinationState.add("MO");
        expectedDestinationCity.add("BRIDGETON");
        expectedDelivery.add("DELIVERED");
        expectedDeliveryStatus.add("DELIVERED // Location - BRIDGETON, MO, US // Date - 09/20/2023 // Time - 111909");
        expectedDeliveryStartDate.add("09/18/2023");
        expectedDeliveryEndDate.add("09/20/2023");
        expectedWarning.add("");
        expectedTimeTaken.add("2");
        expectedTrackerDate.add("09/20/2023");
        
        assertEquals(expectedTrackingService, frame.arrTrackingService);
        assertEquals(expectedTrackingNos, frame.arrTrackingNos);
        assertEquals(expectedDestinationState, frame.arrDestinationState);
        assertEquals(expectedDestinationCity, frame.arrDestinationCity);
        assertEquals(expectedDelivery, frame.arrDelivery);
        assertEquals(expectedDeliveryStatus, frame.arrDeliveryStatus);
        assertEquals(expectedDeliveryStartDate, frame.arrDeliveryStartDate);
        assertEquals(expectedDeliveryEndDate, frame.arrDeliveryEndDate);
        assertEquals(expectedWarning, frame.arrWarning);
        assertEquals(expectedTimeTaken, frame.arrTimeTaken);
        assertEquals(expectedTrackerDate, frame.arrTrackerDate);
        
        frame.trackingUPS("1ZY5F5030391847305");
        
        expectedTrackingService.add("UPS");
        expectedTrackingNos.add("1ZY5F5030391847305");
        expectedDestinationState.add("");
        expectedDestinationCity.add("");
        expectedDelivery.add("YET TO BE DELIVERED");
        expectedDeliveryStatus.add("Processing at UPS Facility // Location - Boston, MA, US // Date - 09/21/2023 // Time - 062321");
        expectedDeliveryStartDate.add("09/18/2023");
        expectedDeliveryEndDate.add("N/A");
        expectedWarning.add("26 days and not delivered. Please check.");
        expectedTimeTaken.add("");
        expectedTrackerDate.add("09/21/2023");
        
        assertEquals(expectedTrackingService, frame.arrTrackingService);
        assertEquals(expectedTrackingNos, frame.arrTrackingNos);
        assertEquals(expectedDestinationState, frame.arrDestinationState);
        assertEquals(expectedDestinationCity, frame.arrDestinationCity);
        assertEquals(expectedDelivery, frame.arrDelivery);
        assertEquals(expectedDeliveryStatus, frame.arrDeliveryStatus);
        assertEquals(expectedDeliveryStartDate, frame.arrDeliveryStartDate);
        assertEquals(expectedDeliveryEndDate, frame.arrDeliveryEndDate);
        assertEquals(expectedWarning, frame.arrWarning);
        assertEquals(expectedTimeTaken, frame.arrTimeTaken);
        assertEquals(expectedTrackerDate, frame.arrTrackerDate);
    }
    
    @Test
    void testTrackingOldDominion() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
    {
        ShipmentTrackerFrame frame = new ShipmentTrackerFrame();
        
        ArrayList<String> expectedTrackingService = new ArrayList<String>();
    	ArrayList<String> expectedTrackingNos = new ArrayList<String>();
    	ArrayList<String> expectedDestinationState = new ArrayList<String>();
    	ArrayList<String> expectedDestinationCity = new ArrayList<String>();
    	ArrayList<String> expectedDelivery = new ArrayList<String>();
    	ArrayList<String> expectedDeliveryStatus = new ArrayList<String>();
    	ArrayList<String> expectedDeliveryStartDate = new ArrayList<String>();
    	ArrayList<String> expectedDeliveryEndDate = new ArrayList<String>();
    	ArrayList<String> expectedWarning = new ArrayList<String>();
    	ArrayList<String> expectedTimeTaken = new ArrayList<String>();
    	ArrayList<String> expectedTrackerDate = new ArrayList<String>();

        frame.trackingOldDominion("03305154381");
        
        expectedTrackingService.add("Old Dominion LTL");
        expectedTrackingNos.add("03305154381");
        expectedDestinationState.add("NY");
        expectedDestinationCity.add("BROOKLYN");
        expectedDelivery.add("DELIVERED");
        expectedDeliveryStatus.add("DELIVERED // Location - WHITESTONE, NY, US // Date - 09/25/2023 // Time - 08:29:31.000-04:00");
        expectedDeliveryStartDate.add("09/18/2023");
        expectedDeliveryEndDate.add("09/25/2023");
        expectedWarning.add("");
        expectedTimeTaken.add("7");
        expectedTrackerDate.add("09/25/2023");
        
        assertEquals(expectedTrackingService, frame.arrTrackingService);
        assertEquals(expectedTrackingNos, frame.arrTrackingNos);
        assertEquals(expectedDestinationState, frame.arrDestinationState);
        assertEquals(expectedDestinationCity, frame.arrDestinationCity);
        assertEquals(expectedDelivery, frame.arrDelivery);
        assertEquals(expectedDeliveryStatus, frame.arrDeliveryStatus);
        assertEquals(expectedDeliveryStartDate, frame.arrDeliveryStartDate);
        assertEquals(expectedDeliveryEndDate, frame.arrDeliveryEndDate);
        assertEquals(expectedWarning, frame.arrWarning);
        assertEquals(expectedTimeTaken, frame.arrTimeTaken);
        assertEquals(expectedTrackerDate, frame.arrTrackerDate);
    }
}
