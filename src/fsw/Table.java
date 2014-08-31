package fsw;

import java.util.ArrayList; 

/**
 * Define a table. A table has a name and a list of data points. 
 * 
 * @author David McComas
 *
 */
public class Table {

   private String  name;
   private int     pointCnt;  // Number of data points in the packet
   
   private ArrayList<DataPnt> dataPntList = new ArrayList<DataPnt>();
   
   public Table (String name) {
   
      this.name  = name;
      pointCnt   = 0;
      
   } // End Table()
   

   public String getName() {
      
      return name;
   
   } // getName()


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
   
} // End class Table
