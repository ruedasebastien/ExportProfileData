package com;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReadConfig {
	   static Properties configFile;
	   InputStream inputStream;
	   OutputStream outputStream;
	   static String file = "C:\\Users\\srueda\\Desktop\\Daimler Export Tool\\bin\\config.properties";
	   
	   public ReadConfig() throws IOException{
		
		   configFile = new Properties();
		   
		
		   try {
			   inputStream = new FileInputStream(file);
			   System.out.println(inputStream);
				if (inputStream != null) {
					configFile.load(inputStream);
				} else {
					throw new FileNotFoundException("property file '" + file + "' not found in the classpath");
				}
		   	}catch(Exception eta){
			   	System.out.println("Exception: " + eta);
			   	//eta.printStackTrace();
			   	
		   	} finally {
				inputStream.close();
			}
	   }
	 
	   public String getProperty(String key){
		   String value = this.configFile.getProperty(key);
		   return value;
	   }
	   


	public static void UpdateJarStatus(String JarSatus) throws IOException {
	    try {
		   //OutputStream f = new FileOutputStream(file);
		   
		   configFile.setProperty("JarStatus", JarSatus);
		   FileWriter writer = new FileWriter(file);
		
		   configFile.store(writer, "host settings");
		   writer.close();
		} catch (FileNotFoundException ex) {
		    // file does not exist
		} catch (IOException ex) {
		    // I/O error
		}
		
	}
	
}
