package com;

import org.jasypt.util.text.BasicTextEncryptor;

import java.io.IOException;
import java.util.Properties;
import com.ReadConfig;
 
public class ConfigFileEncryption {
    Properties configFile;
    String cytricPassword;
    String cytricUsername;
    ReadConfig cfg ;


    public ConfigFileEncryption() throws IOException {
    	//cytricPassword= GetPassword();
    	cfg = new ReadConfig();
    	//Encryptpassword(" fwef");
    }
   
    public String GetKeyValue(String key) throws IOException {
    	String Value = cfg.getProperty(key);
		return Value;
    }
    
    
    public String GetPassword() throws IOException {
    	String EncryptedPassword = cfg.getProperty("cytricEncryptedPassword");
    	String DecryptedPassword = Decrypt(EncryptedPassword);
    	return DecryptedPassword;
    }
	
	public String Encrypt(String PlainText) {
		BasicTextEncryptor textEncryptor = DefineEncryptor();
		String EncryptedText = textEncryptor.encrypt(PlainText);
		return EncryptedText;
	}
	
	public String Decrypt(String EncryptedText) {
		BasicTextEncryptor textEncryptor = DefineEncryptor();
		String DencryptedText = textEncryptor.decrypt(EncryptedText);
		return DencryptedText;
	}
	
    public BasicTextEncryptor DefineEncryptor(){
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword("myEncryptionPassword");
		return textEncryptor;
    }
    

    /*public void Encryptpassword(String cytricPassword) throws IOException {
    	ReadConfig cfg = new ReadConfig();
    	String Encryptedpassword = Encrypt("Amadeus01!");
    	System.out.println(Encryptedpassword);
    	    	
    }
    */
}
