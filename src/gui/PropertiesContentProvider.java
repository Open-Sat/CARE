package gui;

import java.util.*;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides a JFACE content provider for the Java utility Properties. 
 *
 * @author dcmccom2
 *
 */
public class PropertiesContentProvider implements IStructuredContentProvider {

   /**
    * Called to dispose resources allocated by this class
    * 
    * @param arg0 the parent viewer
    * @param arg1 the old input
    * @param arg2 the new input
    */
   @Override
   public void dispose() {
      
      // We don't create any resources, so we don't dispose any

   } // End dispose()

   /**
    * Called when the input changes
    * 
    * @param arg0 the parent viewer
    * @param arg1 the old input
    * @param arg2 the new input
    */
   @Override
   public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      
      // Nothing to do

   } // End inputChanged()

   /**
    * Gets the Properties elements for the table
    * 
    * @param arg0       Properties object (i.e. the content model)
    * @return Object[]  Array of objects that can be used by the Table label provider
    */
   @Override
   public Object[] getElements(Object arg0) {
      
      return ((Properties) arg0).keySet().toArray();
      
   } // End getElements()

} // End class PropertiesContentProvider
