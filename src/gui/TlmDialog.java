package gui;

import java.util.ArrayList;  
import java.util.Iterator;



import org.apache.log4j.Logger;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;

import ccsds.CcsdsTlmPkt;
import app.CARE;
import app.TlmPageManager;
import app.FswXmlApp;
import fsw.TlmPkt;
import fsw.DataPnt;
import fsw.DataPntStr;

/**
 * 
 * Create a simple modeless dialog for displaying telemetry pages.
 * 
 * @author David McComas
 *
 */
public class TlmDialog extends  Dialog {

   private static Logger logger=Logger.getLogger(TlmDialog.class);

   protected FswXmlApp  fswXmlApp;
   protected TlmPkt     tlmPkt; 
   
   protected Label   timeLabel;
   protected Label   seqCntLabel;
   
   protected TlmPageManager tlmPageManager;
   
   /**
   private final Vector<Label> tlmPntLabel = new Vector<Label>(4096);  // TODO - Use a constant 
   private final Vector<Text>  tlmPntValue = new Vector<Text>(4096);
   **/
   private final Label[] dataPntLabel; // = new Label[4096];  // TODO - Use a constant 
   private final Text[]  dataPntValue; // = new Text[4096];
                   
   /**
    * Constructor that hard codes the window styles. The TlmPageManager is
    * required because the telemetry page must be removed when this dialog
    * closed. The telemetry page registration should have been made prior
    * to calling this constructor.
    * 
    * @param parent          Parent shell
    * @param fswXmlApp       FSW application that has been rendered from an XML file           
    * @param tlmPkt          Telemetry packet that will be displayed
    * @param tlmPageManager  Application telemetry page manager.
    */
   public TlmDialog(Shell parent, FswXmlApp fswXmlApp, TlmPkt tlmPkt, TlmPageManager tlmPageManager) {

      this (parent, SWT.RESIZE | SWT.CLOSE | SWT.MODELESS | SWT.TITLE, fswXmlApp, tlmPkt, tlmPageManager);
   
   } // End TlmDialog()

   /**
    * Constructor that allows the caller to override the default styles
    * 
    * @param parent          Parent shell
    * @param style           Window style settings. At a minimum it should include SWT.MODELESS | SWT.TITLE. 
    * @param fswXmlApp       FSW application that has been rendered from an XML file           
    * @param tlmPkt          Telemetry packet that will be displayed
    * @param tlmPageManager  Application telemetry page manager.
    */
   public TlmDialog(Shell parent, int style, FswXmlApp fswXmlApp, TlmPkt tlmPkt, TlmPageManager tlmPageManager) {

     super(parent, style);

     this.fswXmlApp = fswXmlApp;
     this.tlmPkt    = tlmPkt;
     this.tlmPageManager = tlmPageManager;

     dataPntLabel = new Label[DataPntStr.HDR_LEN+tlmPkt.getPointCnt()];
     dataPntValue = new Text[DataPntStr.HDR_LEN+tlmPkt.getPointCnt()];
     
     setText(fswXmlApp.getName() + "'s  " + tlmPkt.getName() + " - 0x" + Integer.toHexString(tlmPkt.getMsgId()));

   } // End TlmDialog()
   
   /**
    * Opens the dialog
    * 
    */
   public void open() {

     // Create the dialog window
     Shell shell = new Shell(getParent(), getStyle());
     shell.setText(getText());
     createContents(shell);
     shell.pack();

     shell.addDisposeListener(new DisposeListener () {
        public void widgetDisposed(DisposeEvent e) {
           logger.trace("Closing TlmDialog");
           tlmPageManager.removePage(tlmPkt.getMsgId());
        }
     });

     Point pt = getParent().getDisplay().getCursorLocation();
     shell.setLocation(pt.x, pt.y);
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
    * Creates the dialog's contents
    * 
    * @param shell the dialog window
    */
   protected void createContents(final Shell shell) {
     
     shell.setLayout(new GridLayout(1, true));
      
     createHeaderContents(shell);

     ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
     //scrolledComposite.setLayout(new GridLayout(2, true));
     scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
     
     Composite dataPntComposite = new Composite(scrolledComposite, SWT.NONE);
     dataPntComposite.setLayout(new GridLayout(2, true));
     dataPntComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
     
     int i = 0;
     ArrayList<DataPnt> dataPntList = tlmPkt.getDataPntList();
     Iterator<DataPnt> dataPntIt = dataPntList.iterator();
     while ( dataPntIt.hasNext() ) {
     
        DataPnt pnt = dataPntIt.next();
     
        /**
        tlmPntLabel.add(i, new Label (shell, SWT.NONE));
        tlmPntLabel.get(i).setText(pnt.getName());
        
        tlmPntValue.add(i, new Text (shell, SWT.BORDER));
        tlmPntValue.get(i).setText(CARE.STR_UNINIT_VAL + i);
        tlmPntValue.get(i).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        **/
        dataPntLabel[i] = new Label (dataPntComposite, SWT.NONE);
        dataPntLabel[i].setText(pnt.getName());
        dataPntLabel[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        dataPntValue[i] = new Text (dataPntComposite, SWT.BORDER);
        dataPntValue[i].setText(CARE.STR_UNINIT_VAL + i);
        dataPntValue[i].setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        i++;
        
     } // End point loop


     scrolledComposite.setContent(dataPntComposite);
     scrolledComposite.setMinSize (400, 100);
     scrolledComposite.setExpandHorizontal(true);
     scrolledComposite.setExpandVertical(true);
     scrolledComposite.setAlwaysShowScrollBars(true);
     //tlmPointComposite.setSize(tlmPointComposite.computeSize(SWT.DEFAULT, 100));

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

   /**
    * Create contents of the telemetry page header. This is private to
    * force all telemetry pages to have the same header look and feel.  
    * 
    * @param shell the dialog window
    */
   private void createHeaderContents(final Shell shell) {
     
     timeLabel = new Label(shell, SWT.NONE | SWT.CENTER);
     timeLabel.setText(CARE.STR_UNINIT_TIME);
     GridData data = new GridData();
     data.horizontalSpan = 2;
     data.horizontalAlignment = SWT.CENTER;
     timeLabel.setLayoutData(data);
   
     seqCntLabel = new Label(shell, SWT.NONE | SWT.CENTER);
     seqCntLabel.setText(CARE.STR_UNINIT_VAL);
     data = new GridData();
     data.horizontalSpan = 2;
     data.horizontalAlignment = SWT.CENTER;
     seqCntLabel.setLayoutData(data);
     
   } // End createHeaderContents()


   /**
   * Update the telemetry page using the caller supplied telemetry packet. array of strings that
   * contain the current telemetry values.
   *
   * TODO -  Lengths should be okay but should add boundary checks 
   * 
   * @param ccsdsTlmPkt  CCSDS telemetry packet containing the most recent packet
   */
   public void updateValues (CcsdsTlmPkt ccsdsTlmPkt) {
    
      logger.trace("TlmDialog updating values for " + Integer.toHexString(tlmPkt.getMsgId()) + 
            " with " + tlmPkt.getPointCnt() + " telemetry points");

      String[] tlmValue = fswXmlApp.getTlmStrArray(ccsdsTlmPkt);

      if (tlmValue != null) {
         
         updateHeader(tlmValue);
         for (int i=0; i < tlmPkt.getPointCnt(); i++) {
            logger.trace("Updating telemetry point [" + i + "] to " + tlmValue[DataPntStr.HDR_LEN + i]);
            //tlmPntValue.get(i).setText(tlmValue[TlmPntStr.HDR_LEN + i]);
            dataPntValue[i].setText(tlmValue[DataPntStr.HDR_LEN + i]);
         }
      
      } // End if tlmValue != null
      else {
         logger.trace("Null tlmValue array");
      }
   } // End updateValues()
   
   /**
    * Update the telemetry packet header portion using the caller supplied
    * array of strings that contain the current telemtry values.  
    * 
    * @param tlmValue  Array of strings that contain the current telemtry values
    */
   public void updateHeader (String[] tlmValue) {
      
      timeLabel.setText("Updated Time");
      seqCntLabel.setText(tlmValue[DataPntStr.IDX_SEQ_CNT]);
      
   } // End updateHeader()
   
} // End class TlmDialog
