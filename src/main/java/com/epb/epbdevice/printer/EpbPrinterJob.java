/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.printer;

import com.epb.epbdevice.beans.PrintPool;
import com.epb.epbdevice.utl.CommonUtility;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author sim_liang
 */
public class EpbPrinterJob {
    //
    //  fields
    //
    private static final Log LOG = LogFactory.getLog(EpbPrinterJob.class);
    private static final String EMPTY = "";
    private String fileRecKey;

    EpbPrinterJob(String fileRecKey) {
        this.fileRecKey = fileRecKey;
    }

    public synchronized void print(List<PrintPool> printerPrintPoolList, String printPort, String printEncoding) {
        try {
            synchronized (this) {
                CommonUtility.printLog("print to net printer:" + printPort + ",size:" + printerPrintPoolList.size());
                String returnMsg = Epbnetprinter.printPosReceipt(printPort, printerPrintPoolList, printEncoding);
                if (!EMPTY.equals(returnMsg)) {
                    CommonUtility.printLog(returnMsg + ", Print key is " + this.fileRecKey);
                    // reprint
                    startPrintPosReceipt(printerPrintPoolList, printPort, printEncoding);
                } else {
                    CommonUtility.printLog("Done");
                }
                notifyAll();
            }
        } catch (Throwable ex) {
            LOG.error("Failed to print", ex);
        }
    }
    
    private void startPrintPosReceipt(final List<PrintPool> printerPrintPoolList, final String printPort, final String printEncoding) {
        // fax report in a thread
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000); // wait 2 seconds
                } catch (InterruptedException ex) {
                    LOG.error(ex);
                }
                String returnMsg = Epbnetprinter.printPosReceipt(printPort, printerPrintPoolList, printEncoding);
                if (!EMPTY.equals(returnMsg)) {
                    CommonUtility.printLog(returnMsg + ", Reprint key is " + fileRecKey);
                } else {
                    CommonUtility.printLog("Reprint Done");
                }
            }
        });

        // start
        thread.start();
    }
}
