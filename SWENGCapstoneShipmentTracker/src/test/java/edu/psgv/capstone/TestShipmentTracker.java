package edu.psgv.capstone;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        frame.arrListCarrierServicesCSV.add("Carrier1");
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
    void testTrackingUPSDelivered() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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
    }
    
    @Test
    void testTrackingUPSNotDelivered() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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

        frame.trackingUPS("1ZY5F5030390069238");
        
        expectedTrackingService.add("UPS");
        expectedTrackingNos.add("1ZY5F5030390069238");
        expectedDestinationState.add("");
        expectedDestinationCity.add("");
        expectedDelivery.add("YET TO BE DELIVERED");
        expectedDeliveryStatus.add("The receiving business was closed and delivery has been rescheduled for the next business day. // Location - Columbus, OH, US // Date - 11/25/2023 // Time - 200300");
        expectedDeliveryStartDate.add("11/22/2023");
        expectedDeliveryEndDate.add("N/A");
        expectedWarning.add("");
        expectedTimeTaken.add("");
        expectedTrackerDate.add("11/25/2023");
        
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
    void testTrackingOldDominionNotDelivered() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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

        frame.trackingOldDominion("03305012936");
        
        expectedTrackingService.add("Old Dominion LTL");
        expectedTrackingNos.add("03305012936");
        expectedDestinationState.add("FL");
        expectedDestinationCity.add("ORLANDO");
        expectedDelivery.add("YET TO BE DELIVERED");
        expectedDeliveryStatus.add("Arrived at MEMPHIS, TN (MFS) // Location - MEMPHIS, TN, US // Date - 11/22/2023 // Time - 21:41:36.000-05:00");
        expectedDeliveryStartDate.add("11/22/2023");
        expectedDeliveryEndDate.add("11/28/2023");
        expectedWarning.add("");
        expectedTimeTaken.add("");
        expectedTrackerDate.add("11/22/2023");
        
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
    void testTrackingOldDominionDelivered() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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
    
    @Test
    void testTrackingEstesDelivered() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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

        frame.trackingEstesTransportation("0771279000");
        
        expectedTrackingService.add("ESTES Transportation");
        expectedTrackingNos.add("0771279000");
        expectedDestinationState.add("NY");
        expectedDestinationCity.add("STATEN ISLAND");
        expectedDelivery.add("DELIVERED");
        expectedDeliveryStatus.add("DELIVERED // Location - STATEN ISLAND, NY, US // Date - 11/07/2023 // Time - 12:16:31-05:00");
        expectedDeliveryStartDate.add("11/03/2023");
        expectedDeliveryEndDate.add("11/07/2023");
        expectedWarning.add("");
        expectedTimeTaken.add("4");
        expectedTrackerDate.add("11/07/2023");
        
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
    void testTrackingEstesNotDelivered() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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

        frame.trackingEstesTransportation("0771279026");
        
        expectedTrackingService.add("ESTES Transportation");
        expectedTrackingNos.add("0771279026");
        expectedDestinationState.add("AZ");
        expectedDestinationCity.add("TOLLESON");
        expectedDelivery.add("YET TO BE DELIVERED");
        expectedDeliveryStatus.add("In Transit - En route to delivery facility at Phoenix, AZ (297) // Date - 11/22/2023 // Time - 13:32:56-07:00");
        expectedDeliveryStartDate.add("11/20/2023");
        expectedDeliveryEndDate.add("11/28/2023");
        expectedWarning.add("");
        expectedTimeTaken.add("");
        expectedTrackerDate.add("11/22/2023");
        
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
    void testTrackingDayton() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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

        frame.trackingDaytonFreight("688332792");
        
        expectedTrackingService.add("DAYTON FREIGHT");
        expectedTrackingNos.add("688332792");
        expectedDestinationState.add("OH");
        expectedDestinationCity.add("DAYTON");
        expectedDelivery.add("DELIVERED");
        expectedDeliveryStatus.add("delivered to destination // Location - SPRINGBORO, OH, US // Date - 04/28/2023 // Time - 12:19:00");
        expectedDeliveryStartDate.add("04/27/2023");
        expectedDeliveryEndDate.add("04/28/2023");
        expectedWarning.add("");
        expectedTimeTaken.add("1");
        expectedTrackerDate.add("04/28/2023");
        
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
    void testTrackingAveritt() throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException 
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

        frame.trackingAverittLTL("0100041955");
        
        expectedTrackingService.add("Averitt LTL");
        expectedTrackingNos.add("0100041955");
        expectedDestinationState.add("PR");
        expectedDestinationCity.add("CAGUAS");
        expectedDelivery.add("DELIVERED");
        expectedDeliveryStatus.add("N/A");
        expectedDeliveryStartDate.add("10/25/2023");
        expectedDeliveryEndDate.add("11/02/2023");
        expectedWarning.add("");
        expectedTimeTaken.add("8");
        expectedTrackerDate.add("N/A");
        
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
    void testMergeSort()
    {
        ShipmentTrackerFrame frame = new ShipmentTrackerFrame();
        
        // Test input data
        ArrayList<String[]> inputArray = new ArrayList<>();
        inputArray.add(new String[]{"01/01/2023", "Data1"});
        inputArray.add(new String[]{"12/15/2022", "Data2"});
        inputArray.add(new String[]{"05/20/2023", "Data3"});
        inputArray.add(new String[]{"08/10/2022", "Data4"});

        // Expected sorted array
        ArrayList<String[]> expectedArray = new ArrayList<>();
        expectedArray.add(new String[]{"05/20/2023", "Data3"});
        expectedArray.add(new String[]{"01/01/2023", "Data1"});
        expectedArray.add(new String[]{"12/15/2022", "Data2"});
        expectedArray.add(new String[]{"08/10/2022", "Data4"});
        
        // Perform merge sort
        frame.mergeSort(inputArray, 0, inputArray.size() - 1);

        // Assert that the array is sorted correctly
        assertArrayEquals(expectedArray.toArray(), inputArray.toArray());
    }
    
    ShipmentTrackerFrame frame = new ShipmentTrackerFrame();

    @BeforeEach
    void setUp() 
    {
        // Initialize the frame and overwrite the strMasterFilePath
        frame = new ShipmentTrackerFrame();
        frame.strMasterFilePath = "C:\\TechdowShipmentTracking\\ShipmentSummaryMasterTest.xlsx";
    }
    
    @Test
    void testWriteExcelFile() throws IOException
    {	
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
		
		frame.arrTrackingNos.add("1ZY5F5030335508723");
        frame.arrTrackingService.add("UPS");
        frame.arrDestinationState.add("WV");
        frame.arrDestinationCity.add("WHEELING");
        frame.arrDelivery.add("DELIVERED");
        frame.arrDeliveryStatus.add("DELIVERED // Location - WHEELING, WV, US // Date - 11/15/2023 // Time - 094014");
        frame.arrTrackerDate.add("11/15/2023");
        frame.arrDeliveryStartDate.add("11/13/2023");
        frame.arrDeliveryEndDate.add("11/15/2023");
        frame.arrTimeTaken.add("2");
        frame.arrWarning.add("");
    	
    	frame.arrListTrackingNosMaster.add("1ZY5F5030322804132");
        frame.arrListCarrierServicesMaster.add("UPS");
        frame.arrListDestStateMaster.add("NC");
        frame.arrListDestCityMaster.add("GREENSBORO");
        frame.arrListDeliveryMaster.add("DELIVERED");
        frame.arrListDeliveryStatMaster.add("DELIVERED // Location - GREENSBORO, NC, US // Date - 11/15/2023 // Time - 081822");
        frame.arrListTrackerDateMaster.add("11/15/2023");
        frame.arrListDeliveryStartDtMaster.add("11/13/2023");
        frame.arrListDeliveryEndDtMaster.add("11/15/2023");
        frame.arrListTimeTakenMaster.add("2");
        frame.arrListWarningMaster.add("");
        
        frame.writeExcelFile();
        
        frame.readMasterFile();
        
        expectedTrackingNos.add("1ZY5F5030322804132");
        expectedTrackingService.add("UPS");
        expectedDestinationState.add("NC");
        expectedDestinationCity.add("GREENSBORO");
        expectedDelivery.add("DELIVERED");
        expectedDeliveryStatus.add("DELIVERED // Location - GREENSBORO, NC, US // Date - 11/15/2023 // Time - 081822");
        expectedTrackerDate.add("11/15/2023");
        expectedDeliveryStartDate.add("11/13/2023");
        expectedDeliveryEndDate.add("11/15/2023");
        expectedTimeTaken.add("2");
        expectedWarning.add("");
        
        expectedTrackingNos.add("1ZY5F5030335508723");
        expectedTrackingService.add("UPS");
        expectedDestinationState.add("WV");
        expectedDestinationCity.add("WHEELING");
        expectedDelivery.add("DELIVERED");
        expectedDeliveryStatus.add("DELIVERED // Location - WHEELING, WV, US // Date - 11/15/2023 // Time - 094014");
        expectedTrackerDate.add("11/15/2023");
        expectedDeliveryStartDate.add("11/13/2023");
        expectedDeliveryEndDate.add("11/15/2023");
        expectedTimeTaken.add("2");
        expectedWarning.add("");
        
        assertEquals(expectedTrackingNos, frame.arrListTrackingNosMaster);
        assertEquals(expectedTrackingService, frame.arrListCarrierServicesMaster);
        assertEquals(expectedDestinationState, frame.arrListDestStateMaster);
        assertEquals(expectedDestinationCity, frame.arrListDestCityMaster);
        assertEquals(expectedDelivery, frame.arrListDeliveryMaster);
        assertEquals(expectedDeliveryStatus, frame.arrListDeliveryStatMaster);
        assertEquals(expectedTrackerDate, frame.arrListTrackerDateMaster);
        assertEquals(expectedDeliveryStartDate, frame.arrListDeliveryStartDtMaster);
        assertEquals(expectedDeliveryEndDate, frame.arrListDeliveryEndDtMaster);
        assertEquals(expectedTimeTaken, frame.arrListTimeTakenMaster);
        assertEquals(expectedWarning, frame.arrListWarningMaster);
    }
}
