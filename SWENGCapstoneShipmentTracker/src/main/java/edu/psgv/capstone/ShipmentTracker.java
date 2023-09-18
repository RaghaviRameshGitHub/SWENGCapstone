package edu.psgv.capstone;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

import com.opencsv.CSVReader;

public class ShipmentTracker 
{
	public static void main(String [] args)
	{
		//Lists that contain the CSV Data
		ArrayList<String> arrListCarrierServicesCSV = new ArrayList<String>();
		ArrayList<String> arrListTrackingNosCSV = new ArrayList<String>();
		
		///////////////////////////////////Reading the input CSV File Starts//////////////////////////////
		try
		{
			CSVReader reader = new CSVReader(new FileReader("C:\\Users\\Raghavi Ramesh\\Desktop\\Penn State\\12) Capstone\\Excels\\ShipmentSummaryCSV.csv"));
			String[] header = reader.readNext();
			
			//Columns that are to be read from CSV, Data fetched using the Header column of CSV
			ArrayList<Integer> columnsToRead = new ArrayList<Integer>();
			columnsToRead.add(Arrays.asList(header).indexOf("Carrier"));
			columnsToRead.add(Arrays.asList(header).indexOf("Tracking"));
			
			String[] nextLine;
			while ((nextLine = reader.readNext()) != null) 
			{
				for (int columnIndex : columnsToRead) 
				{
					if(columnsToRead.indexOf(columnIndex) == 0)
				  	{
						String strCarrierService1 = nextLine[columnIndex];
				  		arrListCarrierServicesCSV.add(strCarrierService1);
				  	}
				  	else if(columnsToRead.indexOf(columnIndex) == 1)
				  	{
				  		String strListTrackingNo = nextLine[columnIndex];
				  		arrListTrackingNosCSV.add(strListTrackingNo);
				  	}
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			System.out.println("ERROR - Please choose the right Shipment Summary csv file");
			return;
		}
		
		///////////////////////////Reading the input CSV File Ends//////////////////////////////
		
		System.out.println(arrListCarrierServicesCSV);
		System.out.println(arrListTrackingNosCSV);
	}
}