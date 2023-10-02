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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    Path = file.toString();
                    
                    try
					{
                    	readCSVFile();
					}
					catch (Exception e)
				    {
				    	System.out.println("ERROR - Please choose the right Shipment Summary csv file");
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
					} 
					catch (Exception e1) 
					{
						System.out.println("\nERROR - Master File not available in the path.");
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
                } 
                catch (Exception e) 
                {
                    System.out.println("ERROR - Please contact admin");
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
    
    void trackingUPS(String strTrackingNos) throws IOException, InterruptedException
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
    }
    
    void trackingOldDominion(String strTrackingNos) throws IOException, InterruptedException
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
    }
}
