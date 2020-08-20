package com.automation.maven.readcsv;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import com.automation.maven.readcsv.ReadCSVData;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * A Simple program that reads an Excel file.
 * @author Veera Srinivas Raneru
 *
 */
public class AggregateReptValidation {
	public static Logger logger = Logger.getLogger(BenchmarkValidationAggrRept.class.getName());
	public static Properties prop = null;
	   
	public static void main(String[] args) throws IOException {
	    
		prop = readPropertiesFile("config.properties");
		logger.info("username: "+ prop.getProperty("userId"));
	    logger.info("password: "+ prop.getProperty("password"));
	    convertCsvTOExcel();
			   
		String excelFilePath1 = prop.getProperty("excelPath");
		Boolean process = true, processt = true;
		FileInputStream inputStream1 = new FileInputStream(new File(excelFilePath1));
		String jdbcUrl = prop.getProperty("JdbcUrl");
		String userid = prop.getProperty("userId");
		String password = prop.getProperty("password");
		Statement stmt = null;
		Connection con = null;
		String status = "";
		String error = "";
		String throughput = "";
		String avgspeed = "";
		List labels = new ArrayList();
		List avgspeeds = new ArrayList();
		List errorp = new ArrayList();
		List throughputs = new ArrayList();
		try {
			con = DriverManager.getConnection(jdbcUrl, userid, password);
			logger.info("Connection established");
			logger.info("Creating statement...");
			stmt = con.createStatement();
			String sql;
			sql = prop.getProperty("sqlQuery");
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				error = rs.getString("ERROR");
				throughput = rs.getString("THROUGHPUT");
				avgspeed = rs.getString("AVGSPEED");
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		} 
		HSSFWorkbook workbook1 = new HSSFWorkbook(inputStream1);
		HSSFSheet firstSheet1 = workbook1.getSheetAt(0);
		Iterator<Row> iterator1 = firstSheet1.iterator();
		while (iterator1.hasNext()) {
			Row nextRow1 = iterator1.next();
			Iterator<Cell> cellIterator1 = nextRow1.cellIterator();
			while (cellIterator1.hasNext()) {
				Cell cell1 = cellIterator1.next();
				switch (cell1.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					if (cell1.getColumnIndex() == 0) {
						labels.add(cell1.getStringCellValue());
						logger.info("Labels"+labels);
					}
				}
			}

			Iterator<Cell> cellIterator2 = nextRow1.cellIterator();
			while (cellIterator2.hasNext()) {
				Cell cell2 = cellIterator2.next();
				switch (cell2.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					if (cell2.getColumnIndex() == 2) {
						if (!(cell2.getStringCellValue().trim().equals("Average"))) {
							avgspeeds.add(cell2.getStringCellValue());
							logger.info("avgspeed"+avgspeeds);
						}
					}
				}
			}

			Iterator<Cell> cellIterator3 = nextRow1.cellIterator();
			while (cellIterator3.hasNext()) {
				Cell cell3 = cellIterator3.next();
				switch (cell3.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					if (cell3.getColumnIndex() == 9) {
						if (!(cell3.getStringCellValue().trim().equals("Error %"))) {
							errorp.add(cell3.getStringCellValue());
							logger.info("errorp"+errorp);
						}
					}
				}
			}

			Iterator<Cell> cellIterator4 = nextRow1.cellIterator();
			while (cellIterator4.hasNext()) {
				Cell cell4 = cellIterator4.next();
				switch (cell4.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					if (cell4.getColumnIndex() == 10) {
						if (!(cell4.getStringCellValue().trim().equals("Throughput"))) {
							throughputs.add(cell4.getStringCellValue());
							logger.info("throughputs"+throughputs);
						}
					}
				}
			}
		}
		
		for (int i = 0; i < labels.size(); i++) {
        
			logger.info("Done.." + avgspeeds.get(labels.indexOf(labels.get(i)) - 1));
			logger.info("Done.." + errorp.get(labels.indexOf(labels.get(i)) - 1));
			logger.info("Done.." + throughputs.get(labels.indexOf(labels.get(i)) - 1));
		boolean flg1 = Integer
				.parseInt((String) avgspeeds.get(labels.indexOf(labels.get(i)) - 1)) > (Integer.parseInt(avgspeed));
		boolean flg2 = Double.parseDouble(
				removeper((String) errorp.get(labels.indexOf(labels.get(i)) - 1))) > (Double.parseDouble(error));
		boolean flg3 = Double.parseDouble(
				(String) throughputs.get(labels.indexOf(labels.get(i)) - 1)) > (Double.parseDouble(throughput));
		if (flg1 && flg2 && flg3) {
			process = true;
		} else {
			process = false;
		}
		if (process) {
			status = "success";
			processt =  process && processt;
		} else {
			status = "failure";
			processt =  process && processt;
		}
		
		    insertingValues(con,error,avgspeed,throughput,labels.get(i).toString(),status);
		
	    }	
		
		if(processt)
		{
			
			logger.info("Proceed");
			
	    } else 
		{
	    	logger.info("Dont Proceed");
		}
			
	}

	public static void insertingValues(Connection conn,String error,String avgspeed, String throughput,String label,String status)
	  {
	    try
	    {
	      Calendar calendar = Calendar.getInstance();
	      java.sql.Date currDate = new java.sql.Date(calendar.getTime().getTime());
	      String query = prop.getProperty("insertQuery");
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setString(1, "id");
	      preparedStmt.setString(2, error);
	      preparedStmt.setString(3, throughput);
	      preparedStmt.setString(4, avgspeed);
	      preparedStmt.setDate(5, currDate);
	      preparedStmt.setString(6, label);
	      preparedStmt.setString(7, status);
	      preparedStmt.execute();
	      conn.close();
	    }
	    catch (Exception e)
	    {
	    	logger.info("Got an exception!");
	    	logger.info("Message"+e.getMessage());
	    }
	  }
	
	public static Properties readPropertiesFile(String fileName) throws IOException {
	      FileInputStream fis = null;
	      Properties prop = null;
	      try {
	         fis = new FileInputStream(fileName);
	         prop = new Properties();
	         prop.load(fis);
	      } catch(FileNotFoundException fnfe) {
	         fnfe.printStackTrace();
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      } finally {
	         fis.close();
	      }
	      return prop;
	   }
	
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static String removeper(String str) {
		if (str.charAt(str.length() - 1) == '%') {
			str = str.replace(str.substring(str.length() - 1), "");
			return str;
		} else {
			return str;
		}
	}
	
	public static void convertCsvTOExcel() throws IOException
	{
		   
		        ArrayList arList=null;
		        ArrayList al=null;
		        String fName = prop.getProperty("aggregateIfName");
		        String thisLine;
		        int count=0;
		        FileInputStream fis = new FileInputStream(fName);
				DataInputStream myInput = new DataInputStream(fis);
		        int i=0;
		        arList = new ArrayList();
		        while ((thisLine = myInput.readLine()) != null)
		        {
		            al = new ArrayList();
		            String strar[] = thisLine.split(",");
		            for(int j=0;j<strar.length;j++)
		            {
		                al.add(strar[j]);
		            }
		            arList.add(al);
		            System.out.println();
		            i++;
		        }

		        try
		        {
				    HSSFWorkbook hwb = new HSSFWorkbook();
		            HSSFSheet sheet = hwb.createSheet("new sheet");
		            for(int k=0;k<arList.size();k++)
		            {
		                ArrayList ardata = (ArrayList)arList.get(k);
		                HSSFRow row = sheet.createRow((short) 0+k);
		                for(int p=0;p<ardata.size();p++)
		                {
		                    HSSFCell cell = row.createCell((short) p);
		                    String data = ardata.get(p).toString();
		                    if(data.startsWith("=")){
		                        cell.setCellType(Cell.CELL_TYPE_STRING);
								 data=data.replaceAll("\"", "");
		                        data=data.replaceAll("=", "");
		                        cell.setCellValue(data);
		                    }else if(data.startsWith("\"")){
		                        data=data.replaceAll("\"", "");
		                        cell.setCellType(Cell.CELL_TYPE_STRING);
		                        cell.setCellValue(data);
		                    }else{
		                        data=data.replaceAll("\"", "");
		                        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		                        cell.setCellValue(data);
		                    }
		                }
		            }
		            FileOutputStream fileOut = new FileOutputStream(prop.getProperty("aggregateOfName"));
		            hwb.write(fileOut);
		            fileOut.close();
		            logger.info("Your excel file has been generated");
		        } catch ( Exception ex ) {
		            ex.printStackTrace();
		        } 
		    
	}

}
