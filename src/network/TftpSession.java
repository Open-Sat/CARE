package network;

import java.io.File;    
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

import org.apache.commons.net.tftp.*;
import org.apache.log4j.Logger;

import app.CARE;

public class TftpSession {

   private String  remoteIpAddrStr;
   private boolean clientCreated;
   private boolean serverCreated;
   private InetAddress remoteIpAddr; 
   private TFTPClient client;
   private TFTPServer server;
   private String errString = CARE.STR_UNDEFINED;
   
   private static Logger logger=Logger.getLogger(TftpSession.class);
   
   public TftpSession (String remoteIpAddrStr) {
	   
      // Save the address and try to resolve the hostname when a get or put is performed
	  // This keeps exception handling simpler
      this.remoteIpAddrStr = remoteIpAddrStr;
      logger.debug("TftpSession created for IP Address " + this.remoteIpAddrStr);
    
      clientCreated = false;
      serverCreated = false;
      
   } // End TftpSession()
   
   public TFTPClient getClient() {
	
	   return client;
	   
   } // End getClient()
   
   public InetAddress getIpAddress() {
		
	   return remoteIpAddr;
	   
   } // End getIpAddress()
   
   /*
    * TODO - If server works then remove this function. I had trouble communicating
    *        from cFS to CARE. 
    */
   public boolean createClient() {
	   
	  clientCreated = false;

	  client = new TFTPClient();          // Create our TFTP instance to handle the file transfer.
	  client.setDefaultTimeout(5000); 	  // Transfer Timeout in milliseconds

	  // Open local socket
	  try {
		 
		 remoteIpAddr =  InetAddress.getByName(remoteIpAddrStr); 
	     client.open();
	     clientCreated = true;
	  }
	  catch (SocketException e) {
	
	     errString = "Error: Could not open local UDP socket. " + e.getMessage();
	     return false;
	  }
	  catch (Exception e) { // TODO -UnkownHostUnkownHost
			
	         errString = "Error: Could not open local UDP socket. " + e.getMessage();
			 return false;
	  }
	  
      return clientCreated;
      
   } // End createClient()


   public boolean getFile(String localFileName, String remoteFileName) {
   
	  boolean fileClosed = false;
      int transferMode = TFTP.BINARY_MODE;

	  client = new TFTPClient();            // Create our TFTP instance to handle the file transfer.
	  client.setDefaultTimeout(10000); 	  // Transfer Timeout in milliseconds

	  // Open local socket
	  try {
		 
		 remoteIpAddr =  InetAddress.getByName(remoteIpAddrStr); 
	     client.open();
	     
	  }
	  catch (SocketException e) {
	
	     errString = "Error: Could not open local UDP socket. " + e.getMessage();
	     return false;
	  }
	  catch (Exception e) { // todo -UnkownHostUnkownHost
			
	         errString = "Error: Could not open local UDP socket. " + e.getMessage();
			 return false;
	  }
	  
	  
	  FileOutputStream localFileStream = null;
	  File localFile = new File(localFileName);

      if (localFile.exists()) {
        	
   	     errString = "Error: " + localFileName + " already exists.";
	     return false;
      
      }

        // Try to open local file for writing
      try {
            
         localFileStream = new FileOutputStream(localFile);
      
      }
      catch (IOException e) {
    	  
         client.close();
         errString = "Error: could not open local file for writing. " + e.getMessage();
         return false;
        
      }

      // Try to receive remote file via TFTP
      try {
        	
         client.receiveFile(remoteFileName, transferMode, localFileStream, remoteIpAddr);
        
      }
      catch (UnknownHostException e) {
        	
         errString = "Error: could not resolve hostname." + e.getMessage();
         return false;
        
      }
      catch (IOException e) {
        	
         errString = "Error: I/O exception occurred while receiving file." + e.getMessage();
         return false;

      }
      finally {
    	  
         // Close local socket and output file
         client.close();
         try {
         
            if (localFileStream != null) {
               localFileStream.close();
            }
            fileClosed = true;
         }
         catch (IOException e) {
        	 
            fileClosed = false;
            errString = "Error: error closing file." + e.getMessage();
         }
      
      } // End finally

      if (!fileClosed) {
            System.exit(1);
        }

      return fileClosed;
      
   } // End getFile()

   public boolean putFile(String localFileName, String remoteFileName) {
   
      boolean fileClosed = false;
      int transferMode = TFTP.BINARY_MODE;

	  client = new TFTPClient();          // Create our TFTP instance to handle the file transfer.

      logger.debug("TftpSession putFile() - After create TFTP client");

      // Open local socket
	  try {
		 
		 remoteIpAddr =  InetAddress.getByName(remoteIpAddrStr); 
	     client.open(1237,InetAddress.getByName("127.000.000.001")); // TODO - Add port to config file 
		 client.setDefaultTimeout(5000); 	                         // Transfer Timeout in milliseconds
	     logger.debug("TftpSession putFile() - After local open. IP="+client.getLocalAddress()+", Port="+client.getLocalPort());
	  }
	  catch (SocketException e) {
	
	     errString = "Error: Could not open local UDP socket. " + e.getMessage();
	     return false;
	  }
	  catch (Exception e) { // TODO - UnkownHostUnkownHost
			
         errString = "Error: Could not open local UDP socket. " + e.getMessage();
		 return false;
	  }

	  FileInputStream localFile = null;

      // Try to open local file for reading
      try {
      
         localFile = new FileInputStream(localFileName);
	     logger.debug("TftpSession putFile() - After local file open.");
      
      }
      catch (IOException e) {
    	  
         client.close();
         errString = "Error: Could not open local file for reading. " + e.getMessage();
         return false;
      
      }

      // Try to send local file via TFTP
      try {
       
 	     logger.debug("TftpSession putFile() - Before send file: RemoteFile="+remoteFileName+", TransferMode="+transferMode+", LocalFile="+localFileName+", remoteIP="+remoteIpAddr);
         client.sendFile(remoteFileName, transferMode, localFile, remoteIpAddr, client.DEFAULT_PORT);
 	     logger.debug("TftpSession putFile() - After send file.");

      }
      catch (UnknownHostException e) {
    	  
    	  errString = "Error: Could not resolve host name. " + e.getMessage();
          return false;
      
      }
      catch (IOException e) {
    	  
    	  errString = "Error: I/O exception occurred while sending file. " + e.getMessage();
          return false;
      
      }
      finally {
          
         // Close local socket and input file
         client.close();
         try {
              
            if (localFile != null) {
            	localFile.close();
            }
            fileClosed = true;
            
         }
         catch (IOException e) {
        	 
        	fileClosed = false;
            errString = "Error: Coud not close file. " + e.getMessage();
            
         }
      } // End finally

      return fileClosed;

  } // End putFile()

   public boolean putFile2(String localFileName, String remoteFileName) {

      FileInputStream input = null;
	  // Try to open local file for reading
      try
      {
         input = new FileInputStream(localFileName);
      }
      catch (IOException e)
      {
         System.err.println("Error: could not open local file for reading.");
         errString = "Error: Coud not open local file " + localFileName + " for reading. " + e.getMessage();
      }
    
 	  boolean fileClosed = false;
	  int transferMode = TFTP.BINARY_MODE;

	  client = new TFTPClient();          // Create our TFTP instance to handle the file transfer.

	  logger.debug("TftpSession putFile() - After create TFTP client");

      // Open local socket
	  try {
		 
		 remoteIpAddr =  InetAddress.getByName(remoteIpAddrStr); 
	     client.open(1237,InetAddress.getByName("127.000.000.001")); // TODO - Add port to config file 
		 client.setDefaultTimeout(5000); 	                         // Transfer Timeout in milliseconds
	     logger.debug("TftpSession putFile() - After local open. IP="+client.getLocalAddress()+", Port="+client.getLocalPort());
	  }
	  catch (SocketException e) {
	
	     errString = "Error: Could not open local UDP socket. " + e.getMessage();
	     return false;
	  }
	  catch (Exception e) { // TODO - UnkownHostUnkownHost
			
         errString = "Error: Could not open local UDP socket. " + e.getMessage();
		 return false;
	  }

	  FileInputStream localFile = null;

      // Try to open local file for reading
      try {
      
         localFile = new FileInputStream(localFileName);
	     logger.debug("TftpSession putFile() - After local file open.");
      
      }
      catch (IOException e) {
    	  
         client.close();
         errString = "Error: Could not open local file for reading. " + e.getMessage();
         return false;
      
      }

      // Try to send local file via TFTP
      try {
       
 	     logger.debug("TftpSession putFile() - Before send file: RemoteFile="+remoteFileName+", TransferMode="+transferMode+", LocalFile="+localFileName+", remoteIP="+remoteIpAddr);
         client.sendFile(remoteFileName, transferMode, localFile, remoteIpAddr, client.DEFAULT_PORT);
 	     logger.debug("TftpSession putFile() - After send file.");

      }
      catch (UnknownHostException e) {
    	  
    	  errString = "Error: Could not resolve host name. " + e.getMessage();
          return false;
      
      }
      catch (IOException e) {
    	  
    	  errString = "Error: I/O exception occurred while sending file. " + e.getMessage();
          return false;
      
      }
      finally {
          
         // Close local socket and input file
         client.close();
         try {
              
            if (localFile != null) {
            	localFile.close();
            }
            fileClosed = true;
            
         }
         catch (IOException e) {
        	 
        	fileClosed = false;
            errString = "Error: Coud not close file. " + e.getMessage();
            
         }
      } // End finally

      return fileClosed;

   } // End putFile()

   public String getErrString () {
	   
      return errString;
	   
   } // End getErrString()
   
    
} // End class TftpSession
