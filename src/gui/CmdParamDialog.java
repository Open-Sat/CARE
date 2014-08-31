package gui;

import java.util.Iterator; 

import org.apache.log4j.Logger;
import org.eclipse.swt.*; 
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import fsw.CmdParam;
import fsw.CmdStrParam;
import fsw.CmdPkt;
import fsw.FswApp;

/**
 * 
 * Creates a dialog window that is customized according to the {@link CmdPkt} 
 * that is passed to the {@link #open()} method.  
 * 
 * @author David McComas
 *
 */
public class CmdParamDialog extends Dialog {

   private static Logger logger=Logger.getLogger(CmdParamDialog.class);

   boolean validCmd = false;
   
   CmdPkt cmdPkt;
   Text[] paramText = new Text[FswApp.CMD_MAX_PARAM];
     
   /**
    * Constructor that hard codes the window styles.
    * 
    * @param parent Parent shell
    * 
    */
   public CmdParamDialog(Shell parent) {
        // Pass the default styles here
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
   }
   
   /**
    * Constructor that let's caller define the window styles.
    * 
    * @param parent  Parent shell
    * @param style   Window style settings.
    */
   public CmdParamDialog(Shell parent, int style) {
        // Let users override the default styles
        super(parent, style);
        setText("Test Command...");
   }

   /**
    * Open the dialog window and create the contents based on
    * the caller supplied CmdPkt.
    * 
    * @param cmdPkt  Command packet object.
    */
   public boolean open(CmdPkt cmdPkt) {
   
        this.cmdPkt = cmdPkt;
        // Create the dialog window
        Shell shell = new Shell(getParent(), getStyle());
        shell.setText(getText());
        createContents(shell);
        shell.pack();
        shell.open();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) {
            display.sleep();
          }
        }
        
        return validCmd;
        
      }// End open()

      /**
       * Create the dialog contents.
       * 
       * @param shell  Parent shell. 
       */
      private void createContents(final Shell shell) {

         shell.setLayout(new GridLayout(2, false));

         int paramIndex = 0;
         Iterator<CmdParam> cmdParamIt = cmdPkt.getParamList().iterator();
         while ( cmdParamIt.hasNext() ) {
            
            CmdParam  cmdParam = cmdParamIt.next(); 
            
            Label txtLabel = new Label(shell, SWT.NONE);
            txtLabel.setText(cmdParam.getName());
            GridData data = new GridData();
            txtLabel.setLayoutData(data);

            Text inputText = new Text(shell, SWT.BORDER);
            inputText.setText(cmdParam.getValue());
            data = new GridData(GridData.FILL_HORIZONTAL);
            inputText.setLayoutData(data);
            
            paramText[paramIndex++] = inputText;
            
            
         } // End CmdParam loop

        // OK button handler
        Button ok = new Button(shell, SWT.PUSH);
        ok.setText("OK");
        GridData okData = new GridData(GridData.FILL_HORIZONTAL);
        ok.setLayoutData(okData);
        ok.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent event) {
           loadUserSettings();
           shell.close();
          }
        });

        // Cancel button handler
        Button cancel = new Button(shell, SWT.PUSH);
        cancel.setText("Cancel");
        GridData cancelData = new GridData(GridData.FILL_HORIZONTAL);
        cancel.setLayoutData(cancelData);
        cancel.addSelectionListener(new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
             // Ignore any user settings  
             shell.close();
          }
        });

        // Set the OK button as the default, so
        // user can type input and press Enter
        // to dismiss
        shell.setDefaultButton(ok);
        
      } // End createContents()  

      // TODO - Add error protection
      private void loadUserSettings() {

         logger.trace("CmdParamDialog:loadUserSettings");
         int paramIndex = 0;
         Iterator<CmdParam> cmdParamIt = cmdPkt.getParamList().iterator();
         while ( cmdParamIt.hasNext() ) {
            CmdParam  cmdParam = cmdParamIt.next(); 
            if (cmdParam.getType() ==  CmdStrParam.ParamType.STR)
               cmdParam.setValue(paramText[paramIndex].getText().trim()+'\0');
            else
               cmdParam.setValue(paramText[paramIndex].getText());
            logger.trace("CmdParamDialog:Param["+paramIndex+"] = "+paramText[paramIndex].getText());
            paramIndex++;
         } // End CmdParam loop

         validCmd = true;
      
      } // End loadUserSettings()
      
} // End class CmdParamDialog
