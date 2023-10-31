package edu.psgv.capstone;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class ShipmentTrackerFrame extends JFrame implements ActionListener 
{
    String Path;
    String strMasterFilePath = "C:\\TechdowShipmentTracking\\ShipmentSummaryMaster.xlsx";

    //Lists that contain the CSV Data
    ArrayList<String> arrListCarrierServicesCSV = new ArrayList<String>();
	ArrayList<String> arrListTrackingNosCSV = new ArrayList<String>();
	
	//Lists of CSV Data without duplicates
	ArrayList<String> arrListCarrierServicesInput = new ArrayList<String>();
	ArrayList<String> arrListTrackingNosInput = new ArrayList<String>();
	
	//Lists of Data from master file
	ArrayList<String> arrListTrackingNosMaster = new ArrayList<String>();
	ArrayList<String> arrListCarrierServicesMaster = new ArrayList<String>();
	ArrayList<String> arrListDestStateMaster = new ArrayList<String>();
	ArrayList<String> arrListDestCityMaster = new ArrayList<String>();
	ArrayList<String> arrListDeliveryMaster = new ArrayList<String>();
	ArrayList<String> arrListDeliveryStatMaster = new ArrayList<String>();
	ArrayList<String> arrListTrackerDateMaster = new ArrayList<String>();
	ArrayList<String> arrListDeliveryStartDtMaster = new ArrayList<String>();
	ArrayList<String> arrListDeliveryEndDtMaster = new ArrayList<String>();
	ArrayList<String> arrListTimeTakenMaster = new ArrayList<String>();
	ArrayList<String> arrListWarningMaster = new ArrayList<String>();
	
	//Lists of Data that is to be checked after merging csv and master as per requirement
	ArrayList<String> arrListCarrierServices = new ArrayList<String>();
	ArrayList<String> arrListTrackingNos = new ArrayList<String>();
	
	//Lists of output data
	ArrayList<String> arrTrackingService = new ArrayList<String>();
	ArrayList<String> arrTrackingNos = new ArrayList<String>();
	ArrayList<String> arrDestinationState = new ArrayList<String>();
	ArrayList<String> arrDestinationCity = new ArrayList<String>();
	ArrayList<String> arrDelivery = new ArrayList<String>();
	ArrayList<String> arrDeliveryStatus = new ArrayList<String>();
	ArrayList<String> arrDeliveryStartDate = new ArrayList<String>();
	ArrayList<String> arrDeliveryEndDate = new ArrayList<String>();
	ArrayList<String> arrWarning = new ArrayList<String>();
	ArrayList<String> arrTimeTaken = new ArrayList<String>();
	ArrayList<String> arrTrackerDate = new ArrayList<String>();
	
    private final DataFormatter fmt = new DataFormatter();
    
    String strTrackingNumber = "";
	String strDeliveryCarrier = "";
	String strError = "";

    //private static final long serialVersionUID = 1L;

    JButton button;
    JLabel waitLabel;

    ShipmentTrackerFrame() 
    {
        this.setLayout(null);

        JLabel headingLabel = new JLabel();
        headingLabel.setText("SHIPMENT TRACKER");
        headingLabel.setBounds(175, 5, 300, 20);
        headingLabel.setFont(new Font("Comic Sans", Font.BOLD, 15));
        this.add(headingLabel);

        JLabel label = new JLabel();
        label.setText("Please select the Shipment Summary .csv file");
        label.setBounds(120, 80, 300, 20);
        this.add(label);

        button = new JButton("Select File");
        button.setBounds(200, 100, 100, 20);
        button.addActionListener(this);
        this.add(button);

        this.setSize(500, 300);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ev) 
    {
        if (ev.getSource() == button) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int resultFile = fileChooser.showOpenDialog(null);

            if (resultFile == JFileChooser.APPROVE_OPTION) {
                try 
                {
                	Integer intValue = Integer.valueOf("qwer");
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    Path = file.toString();
                    
                    try
					{
                    	readCSVFile();
					}
					catch (Exception e)
				    {
				    	System.out.println("ERROR - Please choose the right Shipment Summary csv file");
				    	JOptionPane.showOptionDialog(null, "ERROR - Please choose the right Shipment Summary .csv file","", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
				    	return;
				    }
                    
                    //The below for function is to remove duplicates present in the input CSV
                    removeDuplicatesCSV();
                    
                    //The below function read the master files and throws error if master file is not present in location
			        FileInputStream fis = null;
					try 
					{
						fis = new FileInputStream(new File(strMasterFilePath));
						readMasterFile();
						
						System.out.println("\nData from Master Excel:");
						System.out.println(arrListCarrierServicesMaster);
						System.out.println(arrListTrackingNosMaster);
						System.out.println(arrListDestStateMaster);
						System.out.println(arrListDestCityMaster);
						System.out.println(arrListDeliveryMaster);
						System.out.println(arrListDeliveryStatMaster);
						System.out.println(arrListDeliveryStartDtMaster);
						System.out.println(arrListDeliveryEndDtMaster);
						System.out.println(arrListWarningMaster);
						System.out.println(arrListTimeTakenMaster);
						System.out.println(arrListTrackerDateMaster+"\n");
					} 
					catch (Exception e1) 
					{
						System.out.println("\nERROR - Master File not available in the path.");
						JOptionPane.showOptionDialog(null, "ERROR - Master File not available in the path.","", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
				    	return;
					}  

                    compareAndPopulateLists();
                    
                    System.out.println("\nFetching details for the below Tracking IDs... Please wait...\n");
					
					//The tracking IDs are now passed to respective APIs for details
					////////////////////////////API Details Start//////////////////////////////////////////
					for(int i=0; i<arrListTrackingNos.size(); i++)
					{
						try
						{
							String strCarrierService = arrListCarrierServices.get(i);
							String strTrackingNos = arrListTrackingNos.get(i);
							
							System.out.println(strTrackingNos+" - "+strCarrierService);
							
							strTrackingNumber = strTrackingNos;
							strDeliveryCarrier = strCarrierService;
							
							//////////////////////////////UPS Start //////////////////////////////////////
							if(strCarrierService.equals("UPS"))
							{
								trackingUPS(strTrackingNos);
							}
							//////////////////////////////UPS End //////////////////////////////////////
							//////////////////////////////Old Dominion LTL Starts //////////////////////////////////////
							else if(strCarrierService.equals("Old Dominion LTL"))
							{
								trackingOldDominion(strTrackingNos);
							}
							//////////////////////////////Old Dominion LTL End //////////////////////////////////////
							//////////////////////////////Estes Transportation Starts //////////////////////////////////////
							else if(strCarrierService.equals("ESTES Transportation"))
							{
								trackingEstesTransportation(strTrackingNos);
							}
							//////////////////////////////Estes Transportation End //////////////////////////////////////
							//////////////////////////////Dayton Freight Starts //////////////////////////////////////
							else if(strCarrierService.equals("DAYTON FREIGHT"))
							{
								trackingDaytonFreight(strTrackingNos);
							}
							//////////////////////////////Dayton Freight End //////////////////////////////////////
							//////////////////////////////Averitt LTL Starts //////////////////////////////////////
							else if(strCarrierService.equals("Averitt LTL"))
							{
								trackingAverittLTL(strTrackingNos);
							}
							//////////////////////////////Averitt LTL End //////////////////////////////////////
							else
							{
								strError = strError+"\nDetails not available for Tracking ID - "+strTrackingNumber+" ("+strDeliveryCarrier+") \n";
							}
						}
						catch (Exception e)
						{
							strError = strError+"\nDetails not available for Tracking ID - "+strTrackingNumber+" ("+strDeliveryCarrier+") \n";
						}
					}
					System.out.println("\nData retrived:");
					System.out.println(arrTrackingService);
					System.out.println(arrTrackingNos);
					System.out.println(arrDestinationState);
					System.out.println(arrDestinationCity);
					System.out.println(arrDelivery);
					System.out.println(arrDeliveryStatus);
					System.out.println(arrDeliveryStartDate);
					System.out.println(arrDeliveryEndDate);
					System.out.println(arrWarning);
					System.out.println(arrTimeTaken);
					System.out.println(arrTrackerDate);
					System.out.println(strError);
                } 
                catch (Exception e) 
                {
                    System.out.println("ERROR - Please contact admin");
                    JOptionPane.showOptionDialog(null, "ERROR - Please contact admin","", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE, null, new Object[]{}, null);
                    return;
                }
            }
        }
    }

    void readCSVFile() throws IOException, CsvValidationException 
    {
    	CSVReader reader = new CSVReader(new FileReader(Path));
		String[] header = reader.readNext();
        
        //Columns that are to be read from CSV, Data fetched using the Header column of CSV
        ArrayList<Integer> columnsToRead = new ArrayList<Integer>();
        columnsToRead.add(Arrays.asList(header).indexOf("Carrier"));
        columnsToRead.add(Arrays.asList(header).indexOf("Tracking"));
        
        String strCarrierService1 = null;
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) 
        {
            for (int columnIndex : columnsToRead) 
            {
            	if(columnsToRead.indexOf(columnIndex) == 0)
            	{
            		strCarrierService1 = nextLine[columnIndex];
            		arrListCarrierServicesCSV.add(strCarrierService1);
            	}
            	else if(columnsToRead.indexOf(columnIndex) == 1)
            	{
            		String strListTrackingNo = nextLine[columnIndex];
            		
            		// There are a few tracking nos in the wrong format in the CSV
            		//The below if loops are to manually correct those tracking nos
            		
            		if(strCarrierService1.equals("Old Dominion LTL"))
    				{
    					if(strListTrackingNo.length()==10)
    					{
    						arrListTrackingNosCSV.add("0"+strListTrackingNo);
    					}
    					else
    					{
    						arrListTrackingNosCSV.add(strListTrackingNo);
    					}
    				}
    				else if(strCarrierService1.equals("ESTES Transportation"))
    				{
    			    	arrListTrackingNosCSV.add(strListTrackingNo.replace("-", "").substring(0,10));
    				}
    				else if(strCarrierService1.equals("DAYTON FREIGHT"))
    				{
    					if(strListTrackingNo.length()==11)
    					{
    						arrListTrackingNosCSV.add(strListTrackingNo.substring(2, 11));
    					}
    					else
    					{
    						arrListTrackingNosCSV.add(strListTrackingNo);
    					}
    				}
    				else if(strCarrierService1.equals("Averitt LTL"))
    				{
    					strListTrackingNo = strListTrackingNo.replaceAll("\\s", "");
    					if(strListTrackingNo.length()==9)
    					{
    						arrListTrackingNosCSV.add("0"+strListTrackingNo);
    					}
    					else
    					{
    						arrListTrackingNosCSV.add(strListTrackingNo);
    					}
    				}
    				else
    				{
    					arrListTrackingNosCSV.add(strListTrackingNo);
    				}
            	}
            }
        }
        reader.close();
        
        System.out.println("Input Carrier Services with duplicates: " + arrListCarrierServicesCSV);
        System.out.println("Input Tracking Numbers with duplicates: " + arrListTrackingNosCSV);
        
        // All the required data of CSV are stored in arrList***CSV Lists
    }
    
    void removeDuplicatesCSV()
    {
        
        for(int i = 0; i<arrListCarrierServicesCSV.size(); i++)
        {
        	if(!(arrListTrackingNosInput.contains(arrListTrackingNosCSV.get(i))))
        	{
        		arrListCarrierServicesInput.add(arrListCarrierServicesCSV.get(i));
        		arrListTrackingNosInput.add(arrListTrackingNosCSV.get(i));
        	}
        }
        //arrList***Input lists are the Lists with Input CSV data without any duplicates
        
        System.out.println("\nInput Carrier Services without duplicates: " + arrListCarrierServicesInput);
        System.out.println("Input Tracking Numbers without duplicates: " + arrListTrackingNosInput);
    }
    
    void readMasterFile() throws IOException 
    {
    	FileInputStream fis = null;
    	
    	fis = new FileInputStream(new File(strMasterFilePath));
    	
    	
    	XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(fis);
		} catch (IOException e1) {
			e1.printStackTrace();
		}   
		
		XSSFSheet sheet=wb.getSheetAt(0);  
		
		int rowTotal = sheet.getLastRowNum();
		
		//Fetches all data from master file to compare with the CSV file
		for(int i=1; i<= rowTotal; i++)
		{
			Row rowsTracking = sheet.getRow(i); //returns the logical row  
			Cell cellsTracking = rowsTracking.getCell(0); //getting the cell representing the given column  
			String valueTracking = fmt.formatCellValue(cellsTracking);
			arrListTrackingNosMaster.add(valueTracking);
			
			Row rowsCarrierServices = sheet.getRow(i); //returns the logical row  
			Cell cellsCarrierServices = rowsCarrierServices.getCell(1); //getting the cell representing the given column  
			String valueCarrierServices = fmt.formatCellValue(cellsCarrierServices);
			arrListCarrierServicesMaster.add(valueCarrierServices);	
			
			Row rowsDestState = sheet.getRow(i); //returns the logical row  
			Cell cellsDestState = rowsDestState.getCell(2); //getting the cell representing the given column  
			String valueDestState = fmt.formatCellValue(cellsDestState);
			arrListDestStateMaster.add(valueDestState);	
			
			Row rowsDestCity = sheet.getRow(i); //returns the logical row  
			Cell cellsDestCity = rowsDestCity.getCell(3); //getting the cell representing the given column  
			String valueDestCity = fmt.formatCellValue(cellsDestCity);
			arrListDestCityMaster.add(valueDestCity);	
			
			Row rowsDeliveryServices = sheet.getRow(i); //returns the logical row  
			Cell cellsDeliveryServices = rowsDeliveryServices.getCell(4); //getting the cell representing the given column  
			String valueDeliveryServices = fmt.formatCellValue(cellsDeliveryServices);
			arrListDeliveryMaster.add(valueDeliveryServices);
			
			Row rowsDeliveryStatus = sheet.getRow(i); //returns the logical row  
			Cell cellsDeliveryStatus = rowsDeliveryStatus.getCell(5); //getting the cell representing the given column  
			String valueDeliveryStatus = fmt.formatCellValue(cellsDeliveryStatus);
			arrListDeliveryStatMaster.add(valueDeliveryStatus);	
			
			Row rowsTrackerDate = sheet.getRow(i); //returns the logical row  
			Cell cellsTrackerDate = rowsTrackerDate.getCell(6); //getting the cell representing the given column  
			String valueTrackerDate = fmt.formatCellValue(cellsTrackerDate);
			arrListTrackerDateMaster.add(valueTrackerDate);	
			
			Row rowsDeliveryStartDt = sheet.getRow(i); //returns the logical row  
			Cell cellsDeliveryStartDt = rowsDeliveryStartDt.getCell(7); //getting the cell representing the given column  
			String valueDeliveryStartDt = fmt.formatCellValue(cellsDeliveryStartDt);
			arrListDeliveryStartDtMaster.add(valueDeliveryStartDt);
			
			Row rowsDeliveryEndDt = sheet.getRow(i); //returns the logical row  
			Cell cellsDeliveryEndDt = rowsDeliveryEndDt.getCell(8); //getting the cell representing the given column  
			String valueDeliveryEndDt= fmt.formatCellValue(cellsDeliveryEndDt);
			arrListDeliveryEndDtMaster.add(valueDeliveryEndDt);
			
			Row rowsTimeTaken = sheet.getRow(i); //returns the logical row  
			Cell cellsTimeTaken = rowsTimeTaken.getCell(9); //getting the cell representing the given column  
			String valueTimeTaken = fmt.formatCellValue(cellsTimeTaken);
			arrListTimeTakenMaster.add(valueTimeTaken);	
			
			Row rowsWarning = sheet.getRow(i); //returns the logical row  
			Cell cellsWarning = rowsWarning.getCell(10); //getting the cell representing the given column  
			String valueWarning= fmt.formatCellValue(cellsWarning);
			arrListWarningMaster.add(valueWarning);
		}
    }

    void compareAndPopulateLists() 
    {
    	//Data is compared between master file and input csv
		//If csv data is present in master file it first checks whether it is delivered or not
		//If delivered ignores the tracking ID, If not delivered takes into consideration
		//If csv data not present in master file, tracking ID taken into consideration
		for(int i = 0; i<arrListTrackingNosInput.size(); i++)
		{
			if(arrListTrackingNosMaster.contains(arrListTrackingNosInput.get(i)))
			{
				int index = arrListTrackingNosMaster.indexOf(arrListTrackingNosInput.get(i));
				if(arrListDeliveryMaster.get(index).equals("YET TO BE DELIVERED"))
				{
					arrListCarrierServices.add(arrListCarrierServicesInput.get(i));
	            	arrListTrackingNos.add(arrListTrackingNosInput.get(i));
				}
			}
			else
			{
				arrListCarrierServices.add(arrListCarrierServicesInput.get(i));
            	arrListTrackingNos.add(arrListTrackingNosInput.get(i));
			}
		}
		//The required data to be checked are collected in the arrList*** lists
		
		System.out.println("Final List of Carrier Services: " + arrListCarrierServices);
        System.out.println("Final List of Tracking Numbers: " + arrListTrackingNos);
    }
    
    void trackingUPS(String strTrackingNos) throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException
    {
    	HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://onlinetools.ups.com/track/v1/details/"+strTrackingNos+"?locale=en_US"))
				.header("Username", "TechdowAnalytics")
				.header("Password", "GongSiMiMa516!")
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.header("AccessLicenseNumber", "0DD5F980C16E20A0")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		
		HttpResponse<String> response = null;
		
		response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		
		String responseOutput = response.body();
		
		JSONParser parser = new JSONParser();  
		JSONObject jsonResponse = null;
		jsonResponse = (JSONObject) parser.parse(responseOutput);
		JSONObject jsonTrackResponse = (JSONObject)jsonResponse.get("trackResponse");
		JSONArray jsonShipmentArray = (JSONArray) jsonTrackResponse.get("shipment");
		JSONObject jsonShipment = (JSONObject)jsonShipmentArray.get(0);
		
		JSONArray jsonPackageArray = (JSONArray) jsonShipment.get("package");
		JSONObject jsonPackage = (JSONObject)jsonPackageArray.get(0);
		
		JSONArray jsonActivityArray = (JSONArray) jsonPackage.get("activity");
		
		//Recent Shipment Activity
		JSONObject jsonActivityRecent = (JSONObject)jsonActivityArray.get(0);
		
		//Oldest Shipment Activity
		JSONObject jsonActivityOldest = (JSONObject)jsonActivityArray.get(jsonActivityArray.size()-1);
		
		//Formatting Start Date
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");
		
		Date dtStartDate = inputFormat.parse(jsonActivityOldest.get("date").toString());
		String strStartDate = outputFormat.format(dtStartDate);
		
		arrDeliveryStartDate.add(strStartDate);
		
		JSONObject jsonLocation = (JSONObject)jsonActivityRecent.get("location");
		JSONObject jsonAddress = (JSONObject)jsonLocation.get("address");
		
		//Status of Shipment
		JSONObject jsonStatus = (JSONObject)jsonActivityRecent.get("status");
		String strStatusType = jsonStatus.get("type").toString();
		
		Date dtRecentDate = inputFormat.parse(jsonActivityRecent.get("date").toString());
		String strRecentDate = outputFormat.format(dtRecentDate);
		
		if(strStatusType.equals("D"))
		{
			arrDestinationState.add(jsonAddress.get("stateProvince").toString());
			arrDestinationCity.add(jsonAddress.get("city").toString());
			
			arrDeliveryEndDate.add(strRecentDate);
			
			arrDelivery.add("DELIVERED");
			
			arrDeliveryStatus.add("DELIVERED"+
					" // Location - "+jsonAddress.get("city").toString()+", "+jsonAddress.get("stateProvince").toString()+", "+jsonAddress.get("country").toString()+
					" // Date - "+strRecentDate+
					" // Time - "+jsonActivityRecent.get("time").toString());	
			
			arrWarning.add("");
			
			//To find time taken to be delivered
			long diffInMillies = Math.abs(dtRecentDate.getTime() - dtStartDate.getTime());
		    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		     
		     arrTimeTaken.add(String.valueOf(diff));
		}
		else
		{
			arrDestinationState.add("");
			
			arrDestinationCity.add("");
			arrDeliveryEndDate.add("N/A");
			arrDelivery.add("YET TO BE DELIVERED");
			
			arrDeliveryStatus.add(jsonStatus.get("description").toString()+
					" // Location - "+(jsonAddress.get("city")==null?"N/A":jsonAddress.get("city")).toString()+
						(jsonAddress.get("stateProvince")==null?"":", "+jsonAddress.get("stateProvince")).toString()+
						(jsonAddress.get("country")==null?"":", "+jsonAddress.get("country")).toString()+
					" // Date - "+strRecentDate+
					" // Time - "+jsonActivityRecent.get("time").toString());
			
			//Get today's date and update if delivery exceeds 7 days
			LocalDate dateObj = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	        String strTodaydate = dateObj.format(formatter);
	        Date dtTodayDate = null;
	        dtTodayDate = new SimpleDateFormat("MM/dd/yyyy").parse(strTodaydate);
	        
	        long diffInMillies = Math.abs(dtTodayDate.getTime() - dtStartDate.getTime());
	        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	        
	        if(diff > 7)
	        {
	        	arrWarning.add(diff+" days and not delivered. Please check.");
	        }
	        else
	        {
	        	arrWarning.add("");
	        }
	        
	        arrTimeTaken.add("");
		}
		
		arrTrackerDate.add(strRecentDate);
		
		arrTrackingService.add("UPS");
		arrTrackingNos.add(jsonShipment.get("inquiryNumber").toString());
    }
    
    void trackingOldDominion(String strTrackingNos) throws IOException, InterruptedException, org.json.simple.parser.ParseException, ParseException
    {
    	HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://api.odfl.com/tracking/v3.0/shipment.track"))
				.header("content-type", "application/json")
				.method("POST", HttpRequest.BodyPublishers.ofString("{\r\n"
						+ "    \"referenceType\": \"PRO\",\r\n"
						+ "    \"referenceNumber\": \""+strTrackingNos+"\"\r\n"
						+ "}"))
				.build();
		HttpResponse<String> response = null;
		
		response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		
		String responseOutput = response.body();
		
		JSONParser parser = new JSONParser();  
		JSONObject jsonResponse = null;
		jsonResponse = (JSONObject) parser.parse(responseOutput);

		JSONArray jsonTrackArray = (JSONArray) jsonResponse.get("traceInfo");
		JSONObject jsonTrack = (JSONObject)jsonTrackArray.get(0);
		
		
		arrDestinationState.add(jsonTrack.get("destSvcState").toString());
		
		arrDestinationCity.add(jsonTrack.get("destSvcCity").toString());
		
		LocalDate dtDelEndDt = LocalDate.parse(jsonTrack.get("updatedEta").toString());
		
		arrDeliveryEndDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt));
		
		JSONArray jsonTraceDetailArr = (JSONArray) jsonTrack.get("trackTraceDetail");
		JSONObject jsonTrackTrace = (JSONObject)jsonTraceDetailArr.get(0);
		
		JSONObject jsonTrackTraceStartDate = (JSONObject)jsonTraceDetailArr.get(jsonTraceDetailArr.size()-1);
		LocalDate dtDelStartDt = LocalDate.parse(jsonTrackTraceStartDate.get("dateTime").toString().substring(0, 10));
		arrDeliveryStartDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt));
		
		if(jsonTrackTrace.get("status").toString().equals("Delivery Confirmed") || 
				jsonTrackTrace.get("status").toString().equals("Delivered"))
		{
			arrDelivery.add("DELIVERED");
			arrWarning.add("");
			
			//To find time taken to be delivered
			String strStartDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt);
			Date dtStartDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);
			
			String strEndDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt);
			Date dtEndDate = new SimpleDateFormat("MM/dd/yyyy").parse(strEndDate);
			
			long diffInMillies = Math.abs(dtEndDate.getTime() - dtStartDate.getTime());
	        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	        
	        arrTimeTaken.add(String.valueOf(diff));
		}
		else
		{
			arrDelivery.add("YET TO BE DELIVERED");
			
			String strStartDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt);
			Date dtStartDate = null;
			try {
				dtStartDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			LocalDate dateObj = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	        String strTodaydate = dateObj.format(formatter);
	        Date dtTodayDate = null;
			try {
				dtTodayDate = new SimpleDateFormat("MM/dd/yyyy").parse(strTodaydate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        long diffInMillies = Math.abs(dtTodayDate.getTime() - dtStartDate.getTime());
	        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	        
	        if(diff > 7)
	        {
	        	arrWarning.add(diff+" days and not delivered. Please check.");
	        }
	        else
	        {
	        	arrWarning.add("");
	        }
	        
	        arrTimeTaken.add("");
		}
		
		//Only Delivery Confirmed and no Delivered check in below if, so that location of delivery can be derived
		if(jsonTrackTrace.get("status").toString().equals("Delivery Confirmed"))
		{
			JSONObject jsonTrackTraceStatus = (JSONObject)jsonTraceDetailArr.get(1);
			
			LocalDate dtDate = LocalDate.parse(jsonTrackTraceStatus.get("dateTime").toString().substring(0, 10));
			
			arrDeliveryStatus.add(jsonTrackTraceStatus.get("statusDesc").toString().toUpperCase()+
					" // Location - "+jsonTrackTraceStatus.get("city").toString()+
					", "+jsonTrackTraceStatus.get("state").toString()+
					", US // Date - "+DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDate)+
					" // Time - "+jsonTrackTraceStatus.get("dateTime").toString().substring(11, 29));
			
			arrTrackerDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDate));
		}
		else
		{
			LocalDate dtDate = LocalDate.parse(jsonTrackTrace.get("dateTime").toString().substring(0, 10));
			
			if(jsonTrackTrace.get("city") == null)
			{
				arrDeliveryStatus.add(jsonTrackTrace.get("statusDesc").toString()+
					" // Date - "+DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDate)+
					" // Time - "+jsonTrackTrace.get("dateTime").toString().substring(11, 29));
			}
			else
			{
				arrDeliveryStatus.add(jsonTrackTrace.get("statusDesc").toString()+
						" // Location - "+jsonTrackTrace.get("city").toString()+
						", "+jsonTrackTrace.get("state").toString()+
						", US // Date - "+DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDate)+
						" // Time - "+jsonTrackTrace.get("dateTime").toString().substring(11, 29));
			}
			
			arrTrackerDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDate));
		}
		
		arrTrackingService.add("Old Dominion LTL");
		arrTrackingNos.add(jsonResponse.get("referenceNumber").toString());
    }
    
    void trackingEstesTransportation(String strTrackingNos) throws IOException, InterruptedException, org.json.simple.parser.ParseException, ParseException
    {
    	HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://myestes-api.estes-express.com/shipmenttracking/history?pro="+strTrackingNos))
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
    	
		HttpResponse<String> response = null;
		response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		String responseOutput = response.body();
		
		JSONParser parser = new JSONParser();  
		JSONObject jsonResponse = null;
		jsonResponse = (JSONObject) parser.parse(responseOutput);
		
		JSONArray jsonTrackArray = (JSONArray) jsonResponse.get("data");
		JSONObject jsonTrack = (JSONObject)jsonTrackArray.get(0);
		
		JSONObject jsonTrackConsignee = (JSONObject)jsonTrack.get("consigneeParty");
		JSONObject jsonTrackConsAddress = (JSONObject)jsonTrackConsignee.get("address");
		
		arrDestinationState.add(jsonTrackConsAddress.get("state").toString());
		arrDestinationCity.add(jsonTrackConsAddress.get("city").toString());
		
		LocalDate dtDelStartDt = LocalDate.parse(jsonTrack.get("pickupDate").toString());
		
		arrDeliveryStartDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt));
		
		JSONObject jsonTrackStatus = (JSONObject)jsonTrack.get("status");
		if(jsonTrackStatus.get("conciseStatus").toString().equals("Delivered"))
		{
			arrDelivery.add("DELIVERED");
			
			LocalDate dtDelEndDt = LocalDate.parse(jsonTrackStatus.get("referenceDate").toString());
			
			arrDeliveryStatus.add(jsonTrackStatus.get("conciseStatus").toString().toUpperCase()+
					" // Location - "+jsonTrackConsAddress.get("city").toString()+
					", "+jsonTrackConsAddress.get("state").toString()+
					", "+jsonTrackConsAddress.get("country").toString()+
					" // Date - "+DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt)+
					" // Time - "+jsonTrackStatus.get("referenceTime").toString());
			
			
			arrDeliveryEndDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt));
		
			arrWarning.add("");
			
			
			//To find time taken to be delivered
			String strStartDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt);
			Date dtStartDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);
			
			String strEndDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt);
			Date dtEndDate = new SimpleDateFormat("MM/dd/yyyy").parse(strEndDate);
			
			long diffInMillies = Math.abs(dtEndDate.getTime() - dtStartDate.getTime());
	        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	        
	        arrTimeTaken.add(String.valueOf(diff));
	        
	        arrTrackerDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt));
		}
		else
		{
			arrDelivery.add("YET TO BE DELIVERED");
			
			LocalDate dtDate = LocalDate.parse(jsonTrackStatus.get("referenceDate").toString());
			
			arrDeliveryStatus.add(jsonTrackStatus.get("conciseStatus").toString()+
					" - "+jsonTrackStatus.get("expandedStatus").toString()+
					" // Date - "+DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDate)+
					" // Time - "+jsonTrackStatus.get("referenceTime").toString());
			
			JSONObject jsonEstimatedDelivery = (JSONObject)jsonTrack.get("estimatedDelivery");
			LocalDate dtDelEndDt = LocalDate.parse(jsonEstimatedDelivery.get("startDate").toString());
			arrDeliveryEndDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt));
		
			String strStartDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt);
			Date dtStartDate = null;
			dtStartDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);
			
			LocalDate dateObj = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	        String strTodaydate = dateObj.format(formatter);
	        Date dtTodayDate = null;
	        dtTodayDate = new SimpleDateFormat("MM/dd/yyyy").parse(strTodaydate);
	        
	        long diffInMillies = Math.abs(dtTodayDate.getTime() - dtStartDate.getTime());
	        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	        
	        if(diff > 7)
	        {
	        	arrWarning.add(diff+" days and not delivered. Please check.");
	        }
	        else
	        {
	        	arrWarning.add("");
	        }
	        arrTimeTaken.add("");
	        
	        arrTrackerDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDate));
		}
		
		arrTrackingService.add("ESTES Transportation");
		arrTrackingNos.add(jsonTrack.get("pro").toString());
    }
    
    void trackingDaytonFreight(String strTrackingNos) throws IOException, InterruptedException, ParseException, org.json.simple.parser.ParseException
    {
    	HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://tools.daytonfreight.com/tracking/detail/"+strTrackingNos))
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		
		HttpResponse<String> response = null;
		response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		String responseOutput = response.body();
		
		String result = responseOutput.substring(responseOutput.indexOf("<tracking-detail") + 24, responseOutput.indexOf("</tracking-detail>") - 2).replace("&quot;", "\"");
		
		JSONParser parser = new JSONParser();  
		JSONObject jsonResponse = null;
		jsonResponse = (JSONObject) parser.parse(result);
		JSONObject jsonTrack = (JSONObject)jsonResponse.get("trackingResult");
		
		JSONObject jsonDestinationState = (JSONObject)jsonTrack.get("destinationServiceCenter");
		arrDestinationState.add(jsonDestinationState.get("state").toString());
		arrDestinationCity.add(jsonDestinationState.get("city").toString());
		
		LocalDate dtDelStartDt = LocalDate.parse(jsonTrack.get("pickupDate").toString().substring(0, 10));
		arrDeliveryStartDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt));
		
		
		JSONObject jsonStatus = (JSONObject)jsonTrack.get("status");
		if(jsonStatus.get("activityCode").equals("DLV"))
		{
			arrDelivery.add("DELIVERED");
			arrWarning.add("");
			
			//To find time taken to be delivered
			String strStartDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt);
			Date dtStartDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);
			
			LocalDate dtDelEndDt = LocalDate.parse(jsonTrack.get("deliveryDate").toString().substring(0, 10));
			String strEndDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt);
			Date dtEndDate = new SimpleDateFormat("MM/dd/yyyy").parse(strEndDate);
			
			long diffInMillies = Math.abs(dtEndDate.getTime() - dtStartDate.getTime());
	        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	        
	        arrTimeTaken.add(String.valueOf(diff));
		}
		else
		{
			arrDelivery.add("YET TO BE DELIVERED");
			
			String strStartDate = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelStartDt);
			Date dtStartDate = null;
			try {
				dtStartDate = new SimpleDateFormat("MM/dd/yyyy").parse(strStartDate);
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			LocalDate dateObj = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	        String strTodaydate = dateObj.format(formatter);
	        Date dtTodayDate = null;
	        dtTodayDate = new SimpleDateFormat("MM/dd/yyyy").parse(strTodaydate);
	        
	        long diffInMillies = Math.abs(dtTodayDate.getTime() - dtStartDate.getTime());
	        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	        
	        if(diff > 7)
	        {
	        	arrWarning.add(diff+" days and not delivered. Please check.");
	        }
	        else
	        {
	        	arrWarning.add("");
	        }
	        
	        arrTimeTaken.add("");
		}
		
		LocalDate dtCurrentDt = LocalDate.parse(jsonStatus.get("time").toString().substring(0, 10));
		
		arrDeliveryStatus.add(jsonStatus.get("activity").toString()+
				" // Location - "+jsonStatus.get("city").toString()+ ", " +
				jsonStatus.get("state").toString()+", US" +
				" // Date - "+DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtCurrentDt)+
				" // Time - "+jsonStatus.get("time").toString().substring(11, 19));
		
		arrTrackerDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtCurrentDt));
		
		LocalDate dtDelEndDt = LocalDate.parse(jsonTrack.get("deliveryDate").toString().substring(0, 10));
		arrDeliveryEndDate.add(DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH).format(dtDelEndDt));
		
		arrTrackingService.add("DAYTON FREIGHT");
		arrTrackingNos.add(jsonTrack.get("pro").toString());
    }
    
    void trackingAverittLTL(String strTrackingNos) throws IOException, InterruptedException
    {
    	HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://tools.averitt.com/servlet/rsoLTLtrack?content-type=application/json&Number="+strTrackingNos))
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
    	
		HttpResponse<String> response = null;
		response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		String responseOutput = response.body();
    }
}
