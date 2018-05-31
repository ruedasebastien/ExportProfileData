package com;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ReadConfig {
	   static Properties configFile;
	   InputStream inputStream;
	   OutputStream outputStream;
	   static String file = "config.properties";
	   
	   public ReadConfig() throws IOException{
		
		   configFile = new Properties();
		   
		
		   try {
			   inputStream = getClass().getClassLoader().getResourceAsStream(file);
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
	   


	/*public static void SaveFile(String encryptedpassword) throws IOException {
	    try {
		   OutputStream f = new FileOutputStream(file);
		   configFile.save(f, encryptedpassword);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
}
