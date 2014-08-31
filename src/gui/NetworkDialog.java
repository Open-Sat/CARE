package gui;

import org.eclipse.swt.*;   
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Dialog box to configure the network connection to the CFS.
 * 
 * @author David McComas
 *
 */
public class NetworkDialog extends Dialog {

   private String   careIpAddress;
   private String   cfsIpAddress;
   private String   cfsCmdPort;
   private String   cfsTlmPort;

   /**
    * Construct a network dialog. 
    *  
    * @param parent  Parent shell
    */
   public NetworkDialog(Shell parent) {
     
     this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL); // Pass the default styles here
     
   } // End NetworkDialog()
   
   /**
    * Constructor that allows the caller to override the default styles.
    * 
    * @param parent  Parent shell
    * @param style   Window style setting
    */
   public NetworkDialog(Shell parent, int style) {
   
     super(parent, style);         // Let users override the default styles
     setText("Configure the network connection to the CFS ...");
   
   } // End NetworkDialog()

   
   /**
    * Get CARE IP address entered by the user.
    * 
    * @return IP Address
    */
   public String getCareIpAddress() {
      
      return careIpAddress;
      
    } // End getCareIdAddress()

   /**
    * Get Cfs IP address entered by the user.
    * 
    * @return IP Address
    */
   public String getCfsIpAddress() {
      
      return cfsIpAddress;
      
    } // End getCfsIdAddress()

   /**
    * Get CFS command port setting.
    * 
    * @return Command port
    */
   public int getCfsCmdPort() {
   
      return Integer.parseInt(cfsCmdPort);
   
   } // End getCfsCmdPort()

   
   /**
    * Get CFS telemetry port setting.
    * 
    * @return Telemetry port
    */
   public int getCfsTlmPort() {
   
      return Integer.parseInt(cfsTlmPort);

   } // End getCfsTlmPort()

   /**
    * Opens the dialog.
    * 
    * @param ipAddress  CARE IP Address
    * @param ipAddress  CFS IP Address
    * @param cmdPort    CFS Command port number
    * @param tlmPort    CFS Telemetry port number
    * 
    * @return           CARE IP Address
    */
   public String open(String careIpAddress, String cfsIpAddress, String cfsCmdPort, String cfsTlmPort) {
     
     this.careIpAddress = careIpAddress;
     this.cfsIpAddress  = cfsIpAddress;
     this.cfsCmdPort    = cfsCmdPort;
     this.cfsTlmPort    = cfsTlmPort;
     
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
     // Return the entered value, or null
     return getCareIpAddress();
     
   } // End open()

   /**
    * Create the window contents.
    * 
    * @param shell Parent shell
    */
   private void createContents(final Shell shell) {

      shell.setLayout(new GridLayout(2, false));

      // Input for CARE IP Address
      Label careIpLabel = new Label(shell, SWT.NONE);
      careIpLabel.setText("CARE IP Address:");
      GridData data = new GridData();
      careIpLabel.setLayoutData(data);

      // Display the input box
      final Text careIpText = new Text(shell, SWT.BORDER);
      careIpText.setText(careIpAddress);
      data = new GridData(GridData.FILL_HORIZONTAL);
      careIpText.setLayoutData(data);
      
      // Input for CFS IP Address
      Label cfsIpLabel = new Label(shell, SWT.NONE);
      cfsIpLabel.setText("CFS IP Address:");
      data = new GridData();
      cfsIpLabel.setLayoutData(data);

      // Display the input box
      final Text cfsIpText = new Text(shell, SWT.BORDER);
      cfsIpText.setText(cfsIpAddress);
      data = new GridData(GridData.FILL_HORIZONTAL);
      cfsIpText.setLayoutData(data);
      
      // Input for Command Port
      Label cmdLabel = new Label(shell, SWT.NONE);
      cmdLabel.setText("CFS Command Port:");
      data = new GridData();
      cmdLabel.setLayoutData(data);

      // Display the input box
      final Text cfsCmdText = new Text(shell, SWT.BORDER);
      cfsCmdText.setText(cfsCmdPort);
      data = new GridData(GridData.FILL_HORIZONTAL);
      cfsCmdText.setLayoutData(data);

      // Input for Telemetry Port
      Label tlmLabel = new Label(shell, SWT.NONE);
      tlmLabel.setText("CFS Telemetry Port:");
      data = new GridData();
      tlmLabel.setLayoutData(data);

      // Display the input box
      final Text cfsTlmText = new Text(shell, SWT.BORDER);
      cfsTlmText.setText(cfsTlmPort);
      data = new GridData(GridData.FILL_HORIZONTAL);
      cfsTlmText.setLayoutData(data);

     // OK button handler
     Button ok = new Button(shell, SWT.PUSH);
     ok.setText("OK");
     data = new GridData(GridData.FILL_HORIZONTAL);
     ok.setLayoutData(data);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         careIpAddress = careIpText.getText();
         cfsIpAddress  = cfsIpText.getText();
         cfsCmdPort    = cfsCmdText.getText();
         cfsTlmPort    = cfsTlmText.getText();
         shell.close();
       }
     });

     // Cancel button handler
     Button cancel = new Button(shell, SWT.PUSH);
     cancel.setText("Cancel");
     data = new GridData(GridData.FILL_HORIZONTAL);
     cancel.setLayoutData(data);
     cancel.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         careIpAddress = null;
         cfsIpAddress  = null;
         cfsCmdPort    = null;
         cfsTlmPort    = null;
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);
   
   } // End createContents()
   
} // End class NetworkDialog
