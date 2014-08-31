package gui;

import java.util.ArrayList; 
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import app.CARE;
import ccsds.CcsdsTlmPkt;

import fsw.*;
import fswcore.CfeFile;

/**
 * 
 * Create a simple modeless dialog for displaying table files. Table files
 * include the cFE file header.
 * 
 * TODO - Decide whether this dialog is a one time dialog or can remain open
 *        and allow the user to repeatedly dump a file with new values. 
 *        
 * @author David McComas
 *
 */
public class TblFileDialog extends  CfeFileDialog {

   private static Logger logger=Logger.getLogger(TblFileDialog.class);

   protected fsw.Table  table;
   
   protected Label[] hdrLabel;
   protected Text[]  hdrValue;
                   
   private final Label[] dataPntLabel; // = new Label[4096];  // TODO - Use a constant 
   private final Text[]  dataPntValue; // = new Text[4096];

   /**
    * Constructor that hard codes the window styles. 
    * 
    * @param parent   Parent shell
    * @param tblFile  Table file that is being displayed           
    */
   public TblFileDialog(Shell parent, CfeFile tblFile, fsw.Table table) {

      this (parent, SWT.RESIZE | SWT.CLOSE | SWT.MODELESS | SWT.TITLE, tblFile, table);
   
   } // End TblDialog()

   /**
    * Constructor that allows the caller to override the default styles
    * 
    * @param parent    Parent shell
    * @param style     Window style settings. At a minimum it should include SWT.MODELESS | SWT.TITLE. 
    * @param tbllFile  Table file that is being displayed           
    */
   public TblFileDialog(Shell parent, int style, CfeFile tblFile, fsw.Table table) {

     super(parent, style, tblFile);

     this.table = table;

     hdrLabel = new Label[CfeFile.HDR_NUM_FIELDS];
     hdrValue = new Text[CfeFile.HDR_NUM_FIELDS];

     dataPntLabel = new Label[DataPntStr.HDR_LEN + table.getPointCnt()];
     dataPntValue = new Text[DataPntStr.HDR_LEN  + table.getPointCnt()];
     
     setText("Table Display");

   } // End TblDialog()
   
   /**
    * Opens the dialog
    * 
    */
   public void open(String fileName) {

     cfeFile.formatDataStrings(fileName);
     
     // Create the dialog window
     Shell shell = new Shell(getParent(), getStyle());
     shell.setText(getText());
     createContents(shell);
     shell.pack();

     shell.addDisposeListener(new DisposeListener () {
        public void widgetDisposed(DisposeEvent e) {
           logger.trace("Closing TblDialog");
        }
     });

     shell.open();
     /**
     Display display = getParent().getDisplay();
     while (!shell.isDisposed()) {
       if (!display.readAndDispatch()) {
         display.sleep();
       }
     }
     **/
     
   } // End open()

   /**
    * Creates the dialog's contents. This content is basically
    * a CfeFileDialog header and a telemetry page data display. 
    * 
    * @param shell the dialog window
    */
   protected void createContents(final Shell shell) {

     shell.setLayout(new GridLayout(2, true));
      
     createHeaderContents(shell);

     ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
     GridData gridData = new GridData();
     gridData.horizontalSpan = 2;
     gridData.horizontalAlignment = SWT.FILL;
     gridData.grabExcessHorizontalSpace = true;
     gridData.verticalAlignment = SWT.FILL;
     gridData.grabExcessVerticalSpace = true;
     scrolledComposite.setLayoutData(gridData);
     
     Composite dataPntComposite = new Composite(scrolledComposite, SWT.NONE);
     dataPntComposite.setLayout(new GridLayout(2, true));
     dataPntComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
     
     int i = 0;
     String[] tblStrArray = cfeFile.getUserDataStrArray();
     ArrayList<DataPnt> dataPntList = table.getDataPntList();
     Iterator<DataPnt> dataPntIt = dataPntList.iterator();
     while ( dataPntIt.hasNext() ) {
     
        DataPnt pnt = dataPntIt.next();
     
        dataPntLabel[i] = new Label (dataPntComposite, SWT.NONE);
        dataPntLabel[i].setText(pnt.getName());
        dataPntLabel[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        dataPntValue[i] = new Text (dataPntComposite, SWT.BORDER);
        if (tblStrArray[i] == null) {
           dataPntValue[i].setText(CARE.STR_UNINIT_VAL + i);
        }
        else {
           dataPntValue[i].setText(tblStrArray[i]);
        }
        dataPntValue[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        i++;
        
     } // End data point loop

     scrolledComposite.setContent(dataPntComposite);
     scrolledComposite.setMinSize (400, 100);
     scrolledComposite.setExpandHorizontal(true);
     scrolledComposite.setExpandVertical(true);
     scrolledComposite.setAlwaysShowScrollBars(true);

     Composite buttonComposite = new Composite(shell, SWT.NONE);
     buttonComposite.setLayout(new GridLayout(2, true));
     buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

     // Create the OK button and add a handler
     // so that pressing it will set input
     // to the entered value
     Button ok = new Button(buttonComposite, SWT.PUSH);
     ok.setText("OK");
     GridData data = new GridData(GridData.FILL_HORIZONTAL);
     data.horizontalSpan = 1;
     ok.setLayoutData(data);
     ok.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         shell.close();
       }
     });

     // Create the cancel button and add a handler
     // so that pressing it will set input to null
     Button cancel = new Button(buttonComposite, SWT.PUSH);
     cancel.setText("Cancel");
     data = new GridData(GridData.FILL_HORIZONTAL);
     data.horizontalSpan = 1;
     cancel.setLayoutData(data);
     cancel.addSelectionListener(new SelectionAdapter() {
       public void widgetSelected(SelectionEvent event) {
         shell.close();
       }
     });

     // Set the OK button as the default, so
     // user can type input and press Enter
     // to dismiss
     shell.setDefaultButton(ok);

   } // createContents()


} // End class TblFileDialog

