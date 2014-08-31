package gui;

import java.util.*;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * 
 * @author dcmccom2
 *
 */
public class PropertiesLabelProvider implements ITableLabelProvider {

   private Properties prop;
   
   // Constructs a PropertiesLabelProvider
   public PropertiesLabelProvider(Properties prop) {

      this.prop = prop; 
      
   } // End PropertiesLabelProvider()

  /**
    * Adds a listener. This is not supported.
    * 
    * @param arg0 The listener
    */
   @Override
   public void addListener(ILabelProviderListener arg0) {
      
      // Not supported so do nothing

   } // End addListener()

   /**
    * Dispose any created resources
    */
   @Override
   public void dispose() {
      
      // No resources allocated so do nothing

   } // End dispose()

   
   /**
    * Returns whether the specified property, if changed, would affect the label. This is
    * a fixed Properties display so always return false.
    * 
    * @param arg0   The Properties object
    * @param arg1   The specified property
    * @return false
    */
   @Override
   public boolean isLabelProperty(Object arg0, String arg1) {
      
      return false;
      
   } // End isLabelProperty()

   /**
    * Removes the specified listener
    * 
    * @param arg0 the listener
    */
   @Override
   public void removeListener(ILabelProviderListener arg0) {
      
      // Listeners not supported so nothing to do

   } // End removeListener()


   /**
    * Gets the image for the specified column
    * 
    * @param arg0  Properties object
    * @param arg1  Column index
    * @return Image
    */
   @Override
   public Image getColumnImage(Object arg0, int arg1) {
      
      return null;

   } // End getColumnImage()

   /**
    * Gets the text for the specified column
    * 
    * @param arg0  Properties key object
    * @param arg1  Column index
    * @return String containing the text to be displayed
    */
   @Override
   public String getColumnText(Object arg0, int arg1) {
      
      String keyString = arg0.toString();
      String text = "";
      
      switch (arg1) {
      case 0:
        text = keyString;
        break;
      case 1:
        text = prop.getProperty(keyString);
        break;
      }
      return text;
      
   } // End getColumnText()

   /**
    * Sets the current properties object adn must be called prior to
    * TableViewer's setInput() function.  This is a little clumsy because
    * the getColumnText() arg0 does not contain all of the data to be 
    * displayed. Instead it relies on the property object that has the key. 
    * 
    * @param prop
    */
   public void setProperties(Properties prop) {
      
      this.prop = prop;
      
   } // End setProperties()
   
} // End class PropertiesLabelProvider

