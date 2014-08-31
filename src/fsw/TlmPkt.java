package fsw;

import java.util.ArrayList; 

/**
 * Define a telemetry packet. A packet has a name, a unique message ID and a 
 * list of data points. There may be situations when the message ID is not
 * known during construction so a setter method is provided.
 *
 * @author David McComas
 *
 */
public class TlmPkt {

   private String  name;
   private int     msgId;
   private int     pointCnt;  // Number of data points in the packet
   
   private ArrayList<DataPnt> dataPntList = new ArrayList<DataPnt>();
   
   public TlmPkt (String name, Integer msgId) {
   
      this.name  = name;
      this.msgId = msgId;
      pointCnt   = 0;
      
   } // End TlmPkt()
   

   public String getName() {
      
      return name;
   
   } // getName()


   public int getMsgId() {
      
      return msgId;
   
   } // getMsgId()

   public int getPointCnt() {
      
      return pointCnt;
   
   } // getPointCnt()

   public void addDataPnt(DataPnt dataPnt) {
      
      dataPntList.add(dataPnt);
      pointCnt++;
      
   } // End addDataPnt()
   
   public ArrayList<DataPnt> getDataPntList() {
      
      return dataPntList;
   
   } // getDataPntList()

   public void setMsgId(int id) {
	      
      msgId = id;
	   
   } // setMsgId()
   
} // End class TlmPkt
