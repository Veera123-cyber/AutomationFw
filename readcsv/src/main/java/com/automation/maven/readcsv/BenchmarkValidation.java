package com.automation.maven.readcsv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
 
import oracle.jdbc.pool.OracleDataSource;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
/**
 * A dirty simple program that reads an Excel file.
 * @author www.codejava.net
 *
 */
public class BenchmarkValidation {
    public static void main(String[] args) throws IOException {
        String excelFilePath1 = "C:\\Users\\User\\Desktop\\reports\\20200629-testplans_File_Upload2.xlsx";
        Boolean process = true,processt = true;
        FileInputStream inputStream1 = new FileInputStream(new File(excelFilePath1));
        String jdbcUrl = "jdbc:oracle:thin:@dev-1d-db-2.dev.spratingsvpc.com:1521/rdshdev.world";
        String userid = "FGR";
        String password = "fgr";
        Statement stmt = null;
        String error = "";
        String latency = "";
        try {
			Connection con = DriverManager.getConnection(jdbcUrl, userid, password);
			System.out.println("Connection established");
			  //STEP 1 : Execute a query
		      System.out.println("Creating statement...");
		      stmt = con.createStatement();
		      String sql;
		      sql = "SELECT ERROR, LATENCY, AVGSPEED FROM BLC";
		      ResultSet rs = stmt.executeQuery(sql);
		      //STEP 2: Extract data from result set
		      while(rs.next()){
		      error  = rs.getString("ERROR");
		      latency = rs.getString("LATENCY");
		      String avgspeed = rs.getString("AVGSPEED");
		      //Display values
		      }
		      //STEP 3: Clean-up environment
		      rs.close();
		      stmt.close();
		      con.close();
		   }catch(SQLException se){
		      se.printStackTrace();
		   }catch(Exception e){
		      e.printStackTrace();
		   }finally{
		   }//end try

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
                    if(cell1.getColumnIndex()==3)
                    {	
                    	if(!(cell1.getStringCellValue().equals("responseCode")) && (!(cell1.getStringCellValue().equals("Non HTTP response code: java.net.ConnectException"))))
                    	{		
                    	    String val = cell1.getStringCellValue();
                    		if(Integer.parseInt(val) >  Integer.parseInt(error) )
	                    	{		
	                    	 process = false;	
	                     	}
	                        else
	                        {	
	                         process = true;
	                        }  
                    	}	
                    	//}
                    }
                    
                    if(cell1.getColumnIndex()==14)
                    {	
                    	if(!(cell1.getStringCellValue().equals("IdleTime")) )
                    	{		
                    	    String val = cell1.getStringCellValue();
                    		if(Integer.parseInt(val) >  Integer.parseInt(latency) )
	                    	{		
	                    	 process = false;	
	                     	}
	                        else
	                        {	
	                         process = true;
	                        }  
                    	 }	
                    }
                        	
                   }
                          
                }
            processt = process && processt ;
            //break;
        }
        if(processt)
        { 	
           System.out.println("Proceed");
        }
        else
        {
           System.out.println("Dont Proceed");	
        } 	
        inputStream1.close();
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
   
}
  
