package app;
/**
** XmlDoc.xml
*/
import java.io.*;  

import org.apache.log4j.Logger;
import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;

import util.Scheduler;

/**
 * This class wraps an XML file
 */
public class XmlDoc {
   
   private static Logger logger=Logger.getLogger(XmlDoc.class);

   private String filename;
   private Document document;

   /**
    * Constructs an XmlDocument
    * 
    * @param filename
    *           the file name of the XML file
    */
   public XmlDoc(String filename)
   {
      this.filename = filename;
   }

   /**
    * Gets just the file name
    * 
    * @return String
    */
   public String getFilename()
   {
      return filename.substring(filename.lastIndexOf(File.separator) + 1);
   }

   /**
    * Opens and parses the file
    * 
    * @throws IOException
    *            if any problem opening or parsing
    */
   public void open() throws IOException
   {
      SAXBuilder builder = new SAXBuilder();
      try
      {
         document = builder.build(new FileInputStream(filename));
      } catch (JDOMException e)
      {
         throw new IOException(e.getMessage());
      }
   }

   /**
    * Gets the underlying JDOM Document object
    * 
    * @return Document
    */
   public Document getDocument()
   {
      return document;
   }

   /**
    * Output the document, use standard formatter
    */
   public void printDocument()
   {

      try
      {
         XMLOutputter fmt = new XMLOutputter();
         fmt.output(document, System.out);
      } catch (Exception e)
      {
         logger.error("xmlDoc printDocument() exception");
         e.printStackTrace();
      }

   } // End printDocument()

} // End XmlDoc

/* Code snippet that may be useful
List mixedContent = table.getMixedContent();
Iterator i = mixedContent.iterator();
while (i.hasNext()) {
  Object o = i.next();
  if (o instanceof Comment) {
    // Comment has a toString()
    out.println("Comment: " + o);
  }
  else if (o instanceof String) {
    out.println("String: " + o);
  }
  else if (o instanceof ProcessingInstruction) {
    out.println("PI: " + ((ProcessingInstriction)o).getTarget());
  }
  else if (o instanceof Element) {
    out.println("Element: " + ((Element)o).getName());
  }
}
*/