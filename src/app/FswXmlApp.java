package app;

import java.util.ArrayList;       
import java.util.Iterator;
import java.util.Properties;

import ccsds.CcsdsTlmPkt; 
import fsw.*;
import fsw.DataPnt.DspType;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;


/**
 *
 *  This class evolved in conjunction with FswAppProxy and they both were created after FswApp.
 *  FswApp was created as a base class for hand coded FswApps with a convention of XxApp where
 *  Xx is the app's prefix.  XML came after the hand coded apps and instead of removing all of
 *  them I decided to keep the inheritance design even though the FswXmlApp coudln't take 
 *  advantage of the defineCmds() and defineTlm() abstract functions. Maybe a single FswXmlApp
 *  with no inheritance or containment is the final solution. However this evolution made it 
 *  very easy to test the XML in parallel with hand coded apps.
 * 
 *  Notes:
 *  1. This could use some cleanup.  The XML could use attributes for some definitions and better 
 *     error handling so the application reports ill formed XML rather than crashing.
 *
 * @author David McComas
 * 
 */
public class FswXmlApp extends FswApp {

   private static Logger logger=Logger.getLogger(FswXmlApp.class);

   private Document   Doc;
   private Properties midConfig;
   
   //private ArrayList<String>  undefMsgIdList = new ArrayList<String>(); 
   //private ArrayList<TlmPkt>  noIdTlmPktList = new ArrayList<TlmPkt>();  // TlmPkts constructed without a message ID 
   private ArrayList<UndefinedMsgId>  undefMsgIdList = new ArrayList<UndefinedMsgId>();  // Packets constructed without a message ID 
   
   /**
    * 
    * @param doc
    * @param midConfig
    */
   public FswXmlApp(Document doc, Properties midConfig) {
      
      super (doc.getElementsByTagName(CARE.XML_EL_PREFIX).item(0).getTextContent(),
             doc.getElementsByTagName(CARE.XML_EL_NAME).item(0).getTextContent());

      Doc = doc;
      this.midConfig = midConfig;
      
   } // End FswXmlApp()
   
   @Override
   public void defineCmds() {
      
      // The class that contains this class will define the commands
   
   } // End defineCmds()

   @Override
   public void defineTlm() {
      
      // The class that contains this class will define the telemetry packets
   
   } // End defineTlm()

   @Override
   public String getTlmStr(CcsdsTlmPkt TlmMsg) {
      
      // TODO Auto-generated method stub
      return null;
   
   } // End getTlmStr()

   public ArrayList<UndefinedMsgId> getUndefMsgIdList() {
	      
      return undefMsgIdList;
	   
   } // End getUndefMsgIds()
   
   /**
    * XML Assumptions that need to be in user's guide
    * 1. MsgId must be defined before CmdPkt definitions
    * 2. Function Code must be defined before command parameters   
    */         
   public void defineCmdsFromXml() {

      NodeList cmdList  = Doc.getElementsByTagName(CARE.XML_EL_CMDS);
      int      cmdMsgId = FswApp.NULL_CMD_INT;

      logger.trace("defineCmdsFromXml:");

      // Process Command level definitions
      for (int i=0; i < cmdList.getLength(); i++) {
         
         NodeList cmdNodeList = cmdList.item(i).getChildNodes();
         logger.trace("cmd[" + i + "] = " + cmdList.item(i).getNodeName() + ", Len = " + cmdNodeList.getLength());
      
         for (int c=0; c < cmdNodeList.getLength(); c++) {
            
            if (cmdNodeList.item(c).getNodeName().matches(CARE.XML_EL_MSG_ID)) {
               String midStr = midConfig.getProperty(cmdNodeList.item(c).getTextContent());
               if (midStr != null) {
                  cmdMsgId = Integer.parseInt(midStr,16);
                  logger.trace("   Node[" + c + "] = " + cmdNodeList.item(c).getNodeName() + " = " + Integer.toHexString(cmdMsgId));
               }
               else {
                  logger.trace("   Node[" + c + "] = " + cmdNodeList.item(c).getNodeName());
                  logger.error("   Message ID not found in midConfig properties: " + cmdNodeList.item(c).getTextContent());
                  undefMsgIdList.add(new UndefinedMsgId(cmdNodeList.item(c).getTextContent()));
                }
            } // End if message ID
            
            if (cmdNodeList.item(c).getNodeName().matches(CARE.XML_EL_CMD_PKT)) {
               
               processCmdPktNode(cmdNodeList.item(c).getChildNodes(), cmdMsgId);
            }

         } // End cmdNodeList loop
      
      } // End cmdList loop
      
   } // End defineCmdsFromXml()
   
   /**
    * Create a CmdPkt from a <CmdPkt> NodeList.  The CmdPkt constructor 
    * wants the length (in bytes) of all of the command parameters
    * therefore the parameters are stored in a list until all of them
    * have been processed and a total length is known.
    */         
   private void processCmdPktNode(NodeList cmdPktNodeList, int cmdMsgId) {

      CmdPkt   cmdPkt       = null;
      String   cmdName      = CARE.STR_UNDEFINED;
      int      cmdFC        = FswApp.NULL_CMD_INT;
    		  
      int      paramTotLen = 0;
      ArrayList<CmdParam>  cmdParamList = new ArrayList<CmdParam>(); 
       
       
      for (int i=0; i < cmdPktNodeList.getLength(); i++) {
       
         if (cmdPktNodeList.item(i).getNodeName().matches(CARE.XML_EL_NAME)) {
            cmdName = cmdPktNodeList.item(i).getTextContent();
            logger.trace("   Node[" + i + "] = " + cmdPktNodeList.item(i).getNodeName() + " = " + cmdName);
         }
         if (cmdPktNodeList.item(i).getNodeName().matches(CARE.XML_EL_CMD_FC)) {
            cmdFC = Integer.parseInt(cmdPktNodeList.item(i).getTextContent());
            logger.trace("   Node[" + i + "] = " + cmdPktNodeList.item(i).getNodeName() + " = " + cmdFC);
         }
          
         if (cmdPktNodeList.item(i).getNodeName().matches(CARE.XML_EL_CMD_PARAM)) {
             
            NodeList cmdParamNodeList = cmdPktNodeList.item(i).getChildNodes();
                   
            String   paramName   = null;
            String   paramType   = null;
            int      paramLen    = 0;
            String   paramDef    = null;

            NamedNodeMap cmdParamAttributes = cmdPktNodeList.item(i).getAttributes();
            if (cmdParamAttributes != null) {
            
               logger.trace("       Type = " + cmdParamAttributes.getNamedItem(CARE.XML_AT_TYPE) + 
                            "       Len = " + cmdParamAttributes.getNamedItem(CARE.XML_AT_LEN));
               paramType = cmdParamAttributes.getNamedItem(CARE.XML_AT_TYPE).getNodeValue();
               paramLen  = Integer.parseInt(cmdParamAttributes.getNamedItem(CARE.XML_AT_LEN).getNodeValue());

            } // End if has attributes
            for (int p=0; p < cmdParamNodeList.getLength(); p++) {

               logger.trace("   ParamNode[" + p + "] = " + cmdParamNodeList.item(p).getNodeName());
               if (cmdParamNodeList.item(p).getNodeName().matches(CARE.XML_EL_NAME)) {
                  paramName = cmdParamNodeList.item(p).getTextContent();
                  logger.trace("   Name = " + paramName);
               }
               /****
                if (cmdParamNodeList.item(p).getNodeName().matches(JCAT.XML_EL_TYPE)) {
                   paramType = cmdParamNodeList.item(p).getTextContent();
                   logger.trace("   Type = " + paramType);
                }
                if (cmdParamNodeList.item(p).getNodeName().matches(JCAT.XML_EL_LEN)) {
                   paramLen = Integer.valueOf(cmdParamNodeList.item(p).getTextContent());
                   logger.trace("   Len = " + paramLen);
                }
               ****/
               if (cmdParamNodeList.item(p).getNodeName().matches(CARE.XML_EL_DEF)) {
                  paramDef = cmdParamNodeList.item(p).getTextContent();
                  logger.trace("   Default = " + paramDef);
               }
             
            }// End Param loop
             
            if (paramLen > 0) { 
         
               logger.trace("Setting CmdParam: Type=" + paramType +", Len=" + paramLen + ", Default=" + paramDef);
               if (paramType.equalsIgnoreCase(CARE.XML_VAL_INT)) {
                  logger.trace("New int param");
                  cmdParamList.add(new CmdIntParam(paramName,paramDef,paramLen));
               }
               if (paramType.equalsIgnoreCase(CARE.XML_VAL_STR)) {
                  logger.trace("New string param: " + paramName + "," + paramDef + "," + paramLen);
                  cmdParamList.add(new CmdStrParam(paramName,paramDef,paramLen));
               }
               paramTotLen += paramLen;
               
            } // End if paramLen > 0
         
         } // End if have CmdParam element

         cmdPkt = new CmdPkt(Prefix, cmdName, cmdMsgId, cmdFC, paramTotLen); 

         if (paramTotLen > 0) {
            
            Iterator<CmdParam> cmdParamIt = cmdParamList.iterator();
            while ( cmdParamIt.hasNext() ) {
               cmdPkt.addParam(cmdParamIt.next());
            }
            cmdPkt.loadParamList();
            
         } // End if paramTotLen > 0
         
      } // End command loop
       
      if (cmdPkt != null) cmdList.set(cmdFC, cmdPkt);
      
     
    } // End processCmdPktNode()

   /**
    * 
    */
   public void defineTlmFromXml() {
      
      /*
       ** XML Assumptions that need to be in user's guide
       ** 1. Name and MsgId must be defined before DataPnt definitions
       */         

      NodeList tlmList  = Doc.getElementsByTagName(CARE.XML_EL_TLM);

      logger.trace("defineTlmFromXml:");

      // Process Telemetry level definitions
      for (int i=0; i < tlmList.getLength(); i++) {
             
         NodeList tlmNodeList = tlmList.item(i).getChildNodes();
         logger.trace("telemetry[" + i + "] = " + tlmList.item(i).getNodeName() + ", Len = " + tlmNodeList.getLength());
          
         for (int c=0; c < tlmNodeList.getLength(); c++) {
                
            // No telemetry level 
            if (tlmNodeList.item(c).getNodeName().matches(CARE.XML_EL_TLM_PKT)) {
                   
               processTlmPktNode(tlmNodeList.item(c).getChildNodes());
            }

         } // End tlmNodeList loop
          
      } // End tlmList loop
          
   } // End defineTlmFromXml()
       
      
   /*
   ** Create a TlmPkt from a <TlmPkt> NodeList.
   */         
    private void processTlmPktNode(NodeList tlmPktNodeList) {
   
       TlmPkt  tlmPkt      = null;
       String  tlmPktName  = CARE.STR_UNDEFINED;
       int     tlmMsgId    = CARE.UNUSED_MSG_ID;
       
       boolean   firstDataPnt   = true;
       String    dataPntName    = CARE.STR_UNDEFINED;
       String    dataPntType    = CARE.STR_UNDEFINED;
       int       dataPntLen     = 0;
       int       dataPntOffset  = CcsdsTlmPkt.CCSDS_IDX_TLM_DATA;  // Byte offset - Start after header
       int       dataPntBitMask = 0;                               // TODO - Decide whether to support it.
       DspType   dataPntDisplay = DataPnt.DspType.DSP_DEC;         // TODO - Unsupported in XML. Decide whether to support it.
       String    dataPntDescr   = CARE.STR_UNDEFINED;
       DataPntStr dataPntStrFunc = null;
       String    undefinedMsg   = null;
       
       for (int i=0; i < tlmPktNodeList.getLength(); i++) {
        
          if (tlmPktNodeList.item(i).getNodeName().matches(CARE.XML_EL_NAME)) {
             tlmPktName = tlmPktNodeList.item(i).getTextContent();
             logger.trace("   Node[" + i + "] = " + tlmPktNodeList.item(i).getNodeName() + " = " + tlmPktName);
          }
          if (tlmPktNodeList.item(i).getNodeName().matches(CARE.XML_EL_MSG_ID)) {
             String midStr = midConfig.getProperty(tlmPktNodeList.item(i).getTextContent());
             if (midStr != null) {
                tlmMsgId = Integer.parseInt(midStr,16);
                logger.trace("   Node[" + i + "] = " + tlmPktNodeList.item(i).getNodeName() + " = " + Integer.toHexString(tlmMsgId));
             }
             else {
                logger.trace("   Node[" + i + "] = " + tlmPktNodeList.item(i).getNodeName());
                logger.error("   Message ID not found in midConfig properties: " + tlmPktNodeList.item(i).getTextContent());
                undefinedMsg = tlmPktNodeList.item(i).getTextContent();
             }
          } // End if message ID
                  
          if (tlmPktNodeList.item(i).getNodeName().matches(CARE.XML_EL_DATA_PNT)) {
             
             logger.trace("   PntNode[" + i + "] = " + tlmPktNodeList.item(i).getTextContent() +
                          "   hahAttributes = " + tlmPktNodeList.item(i).hasAttributes());
   
             if (firstDataPnt) {
                firstDataPnt = false;
                tlmPkt = new TlmPkt(tlmPktName, tlmMsgId);
                if (tlmMsgId == CARE.UNUSED_MSG_ID) {
                	undefMsgIdList.add(new UndefinedMsgId(undefinedMsg,tlmPkt));
                }
             }
   
             dataPntName = tlmPktNodeList.item(i).getTextContent();
             NamedNodeMap dataPntAttributes = tlmPktNodeList.item(i).getAttributes();
             if (dataPntAttributes != null) {
             
                logger.trace("       Type = " + dataPntAttributes.getNamedItem(CARE.XML_AT_TYPE) + 
                             "       Len = " + dataPntAttributes.getNamedItem(CARE.XML_AT_LEN));
                dataPntType = dataPntAttributes.getNamedItem(CARE.XML_AT_TYPE).getNodeValue();
                dataPntLen  = Integer.parseInt(dataPntAttributes.getNamedItem(CARE.XML_AT_LEN).getNodeValue());
   
             } // End if has attributes
   
             dataPntStrFunc = null;
             if (dataPntType.equalsIgnoreCase(CARE.XML_VAL_UINT)) {
                
                switch (dataPntLen) {
                case 1:
                   dataPntStrFunc = new DataPntStrUint8();
                   break;
                case 2:
                   dataPntStrFunc = new DataPntStrUint16();
                   break;
                case 4:
                   dataPntStrFunc = new DataPntStrUint32();
                   break;
                } // End length switch
   
                
             } // End if type UINT
             else if (dataPntType.equalsIgnoreCase(CARE.XML_VAL_STR)) {
                
                dataPntStrFunc = new DataPntStrStr(dataPntLen);
                
             } // End if type String
             else if (dataPntType.equalsIgnoreCase(CARE.XML_VAL_FLT)) {
                
                dataPntStrFunc = new DataPntStrFloat();
                
             } // End if type Float
   
             if (dataPntStrFunc != null) {
   
                tlmPkt.addDataPnt(new DataPnt(dataPntName, dataPntOffset, dataPntLen, dataPntBitMask, dataPntDisplay, dataPntDescr, dataPntStrFunc));
                dataPntOffset += dataPntLen;
                
             }
             else {
                logger.error("Undefined tlmPntStrFunc for" + dataPntName);
             }
             
          } // End if have datapoint element
   
       } // End telemetry loop
   
       if ((tlmPkt != null) && (tlmMsgId != CARE.UNUSED_MSG_ID)) {
          tlmPktMap.put(tlmMsgId, tlmPkt);
       }
       
    } // End processTlmPktNode()

    /**
     *
     */         
    public void defineTblsFromXml() {
   
       NodeList tblList  = Doc.getElementsByTagName(CARE.XML_EL_TBLS);
   
       logger.trace("defineTablesFromXml:");
   
       // Process Table level definitions
       for (int i=0; i < tblList.getLength(); i++) {
          
          NodeList tblNodeList = tblList.item(i).getChildNodes();
          logger.trace("table[" + i + "] = " + tblList.item(i).getNodeName() + ", Len = " + tblNodeList.getLength());
       
          for (int t=0; t < tblNodeList.getLength(); t++) {
             
             if (tblNodeList.item(t).getNodeName().matches(CARE.XML_EL_TBL)) {
                
                processTableNode(tblNodeList.item(t).getChildNodes());
             }
   
          } // End tblNodeList loop
       
       } // End tblList loop
       
    } // End defineTblsFromXml()
 
    /*
    ** Create a Table from a <Table> NodeList.
    **
    ** - Assumes Table Name is defined prior to data points
    ** - DataPnts and CDATA are mutually exclusive. The code is not bullet 
    **   proof and assumes XML only contains DataPnt elements or a single CDATA element
    */         
     private void processTableNode(NodeList tblNodeList) {
   
        Table   table    = null;
        String  tblName  = CARE.STR_UNDEFINED;
        
        boolean   firstDataPnt   = true;
        String    dataPntName    = CARE.STR_UNDEFINED;
        String    dataPntType    = CARE.STR_UNDEFINED;
        int       dataPntLen     = 0;
        int       dataPntOffset  = 0;
        int       dataPntBitMask = 0;                               // TODO - Decide whether to support it.
        DspType   dataPntDisplay = DataPnt.DspType.DSP_DEC;         // TODO - Unsupported in XML. Decide whether to support it.
        String    dataPntDescr   = CARE.STR_UNDEFINED;
        DataPntStr dataPntStrFunc = null;
        
        for (int i=0; i < tblNodeList.getLength(); i++) {
         
           if (tblNodeList.item(i).getNodeName().matches(CARE.XML_EL_NAME)) {
              tblName = tblNodeList.item(i).getTextContent();
              logger.trace("   Node[" + i + "] = " + tblNodeList.item(i).getNodeName() + " = " + tblName);
           }
           if (tblNodeList.item(i).getNodeName().matches(CARE.XML_EL_DATA_PNT)) {
              
              logger.trace("   PntNode[" + i + "] = " + tblNodeList.item(i).getTextContent() +
                           "   hahAttributes = " + tblNodeList.item(i).hasAttributes());
   
              if (firstDataPnt) {
                 table = new Table(tblName);
                 firstDataPnt = false;
              }
   
              dataPntName = tblNodeList.item(i).getTextContent();
              NamedNodeMap dataPntAttributes = tblNodeList.item(i).getAttributes();
              if (dataPntAttributes != null) {
              
                 logger.trace("       Type = " + dataPntAttributes.getNamedItem(CARE.XML_AT_TYPE) + 
                              "       Len = " + dataPntAttributes.getNamedItem(CARE.XML_AT_LEN));
                 dataPntType = dataPntAttributes.getNamedItem(CARE.XML_AT_TYPE).getNodeValue();
                 dataPntLen  = Integer.parseInt(dataPntAttributes.getNamedItem(CARE.XML_AT_LEN).getNodeValue());
   
              } // End if has attributes
   
              dataPntStrFunc = null;
              if (dataPntType.equalsIgnoreCase(CARE.XML_VAL_UINT)) {
                 
                 switch (dataPntLen) {
                 case 1:
                    dataPntStrFunc = new DataPntStrUint8();
                    break;
                 case 2:
                    dataPntStrFunc = new DataPntStrUint16();
                    break;
                 case 4:
                    dataPntStrFunc = new DataPntStrUint32();
                    break;
                 } // End length switch
   
                 
              } // End if type UINT
              else if (dataPntType.equalsIgnoreCase(CARE.XML_VAL_STR)) {
                 
                 dataPntStrFunc = new DataPntStrStr(dataPntLen);
                 
              } // End if type String
              else if (dataPntType.equalsIgnoreCase(CARE.XML_VAL_FLT)) {
                 
                 dataPntStrFunc = new DataPntStrFloat();
                 
              } // End if type Float
   
              if (dataPntStrFunc != null) {
   
                 table.addDataPnt(new DataPnt(dataPntName, dataPntOffset, dataPntLen, dataPntBitMask, dataPntDisplay, dataPntDescr, dataPntStrFunc));
                 dataPntOffset += dataPntLen;
                 
              }
              else {
                 logger.error("Undefined dataPntStrFunc for" + dataPntName);
              }
              
           } // End if have data point
           if (tblNodeList.item(i).getNodeName().matches(CARE.XML_EL_CDATA)) {
              
              logger.trace("   PntNode[" + i + "] = " + tblNodeList.item(i).getTextContent() +
                           "   hahAttributes = " + tblNodeList.item(i).hasAttributes());
              /*
              ** Having zero data points indicates it is a CDATA table so this logic logs the
              ** error and doesn't fix the situation.
              */ 
              if (!firstDataPnt) {
                 logger.error("Table definitions can't include DataPnt and CDATA elements");
              }
              else {
                 table = new Table(tblName);
              }
              break;
              
           } // End if CDATA element          
   
        } // End table loop
   
        if (table != null) {
           tableMap.put(tblName, table);
        }
   
     } // End processTableNode()

     /**
      * An app's XML definitions may reference message IDs that have not been
      * defined.  This class is used to keep track of these messages and the
      * owner of the FswXmlApp objects can manage getting the definitions.
      */
     public class UndefinedMsgId {
    	 
    	 private String  msg;
    	 private TlmPkt  tlmPkt;
    	 private boolean isTlm;
    	 
    	 UndefinedMsgId(String msg) {
    	
    		this.msg = msg;
    	    isTlm = false;
    	    tlmPkt = null;
    	    
    	 } // End UndefinedMsgId()

    	 UndefinedMsgId(String msg, TlmPkt tlmPkt) {
    		
     		this.msg = msg;
     	    isTlm = true;
     	    this.tlmPkt = tlmPkt;
    		 
    	 } // End UndefinedMsgId()
    	 
    	 public String getMsg() {
    		 return msg;
    	 }
    	 public boolean isTlm() {
    		 return isTlm;
    	 }
         public TlmPkt getTlmPkt() {
    		 return tlmPkt;
    	 }
    	 
     } // End class UndefinedMsgId
     
} // End class FswXmlApp
