/*******************************************************************************
 * 
 * This class provides a bridge between the network package (has no knowledge of CCSDS)
 * and the application/GUI.
 * 
 *******************************************************************************/
package app;

import network.PktWriter;  
import ccsds.*;

/**
 * Manage writing packets to the FSW host platform using a PktWriter.
 * 
 * @author David McComas
 *
 */
public class FswCmdNetwork {

   private static PktWriter  pktOutput;
   
   FswCmdNetwork(String ipAddress, int cmdPort) {
      
      pktOutput = new PktWriter(ipAddress, cmdPort);   
   
   } // End FswCmdNetwork()
   
   /**
    * Send a command to the FSW.
    * 
    * @param CmdPkt
    */
   public void sendCmd(CcsdsCmdPkt cmdPkt) {
      
	  //System.err.println("sendCmd: " + cmdPkt.getByteArray() + ", " + cmdPkt.getTotalLength());
      pktOutput.WriteCmdPkt(cmdPkt.getByteArray(), cmdPkt.getTotalLength());
      
   } // End getPktWriter()
   
   /**
    * Return the PktWriter used to send commands.
    * 
    * @return PktWriter used to send commands. 
    */
   public PktWriter getPktWriter() {
      
      return pktOutput;
      
   } // End getPktWriter()

   /**
    * Return a string containing the command network status.
    * 
    * @return String containing the command network status.
    */
   public String getStatus() {
      
      if (pktOutput != null)
         return pktOutput.getStatus();
      else
         return new String("FswCmdNetwork not initialized");
      
   } // End getStatus()
   
} // End class FswCmdNetwork
