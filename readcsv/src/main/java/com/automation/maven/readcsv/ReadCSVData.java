package com.automation.maven.readcsv;

//Java program to illustrate reading 
//two CSV files 
//with different seperators 

import java.io.FileReader; 
import java.util.List;import org.apache.poi.ss.usermodel.Cell;

import com.opencsv.*; 

public class ReadCSVData { 
	private static final String CSV_FILE_PATH 
		= "C:/Users/User/Desktop/results.csv"; 
	//private static final String CSV_FILE_CUSTOM_SEPERATOR 
		//= "D:\\EclipseWorkSpace\\CSVOperations\\results_semicolon_Seperator.csv"; 

	public static void main(String[] args) 
	{ 

		System.out.println("Read Data Line by Line With Header \n"); 
		readDataLineByLine(CSV_FILE_PATH); 
		System.out.println("_______________________________________________"); 

		System.out.println("Read All Data at Once and Hide the Header also \n"); 
		readAllDataAtOnce(CSV_FILE_PATH); 
		System.out.println("_______________________________________________"); 

/*		System.out.println("Custom Seperator here semi-colon\n"); 
		readDataFromCustomSeperator(CSV_FILE_CUSTOM_SEPERATOR); 
		System.out.println("_______________________________________________"); */
	} 

	public static void readDataLineByLine(String file) 
	{ 

		try { 

			// Create an object of filereader class 
			// with CSV file as a parameter. 
			FileReader filereader = new FileReader(file); 

			// create csvReader object passing 
			// filereader as parameter 
			CSVReader csvReader = new CSVReader(filereader); 
			String[] nextRecord; 
			

			// we are going to read data line by line 
			while ((nextRecord = csvReader.readNext()) != null) { 
				for (String cell : nextRecord) { 
					System.out.print(cell + "\t"); 
					
				} 
				System.out.println(); 
			} 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	} 

	public static void readAllDataAtOnce(String file) 
	{ 
		try { 

			// Create an object of filereader class 
			// with CSV file as a parameter. 
			FileReader filereader = new FileReader(file); 

			// create csvReader object 
			// and skip first Line 
			CSVReader csvReader = new CSVReaderBuilder(filereader) 
									.withSkipLines(1) 
									.build(); 
			List<String[]> allData = csvReader.readAll(); 

			// print Data 
			for (String[] row : allData) { 
				for (String cell : row) { 
					System.out.print(cell + "\t"); 
				} 
				System.out.println(); 
			} 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	} 

	public static void readDataFromCustomSeperator(String file) 
	{ 
		try { 
			// Create object of filereader 
			// class with csv file as parameter. 
			FileReader filereader = new FileReader(file); 

			// create csvParser object with 
			// custom seperator semi-colon 
			CSVParser parser = new CSVParserBuilder().withSeparator(';').build(); 

			// create csvReader object with 
			// parameter filereader and parser 
			CSVReader csvReader = new CSVReaderBuilder(filereader) 
									.withCSVParser(parser) 
									.build(); 

			// Read all data at once 
			List<String[]> allData = csvReader.readAll(); 

			// print Data 
			for (String[] row : allData) { 
				for (String cell : row) { 
					System.out.print(cell + "\t"); 
				} 
				System.out.println(); 
			} 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	} 
} 
