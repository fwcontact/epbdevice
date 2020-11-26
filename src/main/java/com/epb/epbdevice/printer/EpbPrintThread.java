/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.epb.epbdevice.printer;

import com.epb.epbdevice.beans.PrintPool;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sim_liang
 */
public class EpbPrintThread extends Thread {
    EpbPrinterJob printerJob;
    final List<PrintPool> printerPrintPoolList = new ArrayList<PrintPool>();
    String printPort;
    String printEncoding;

    EpbPrintThread(EpbPrinterJob printerJob, List<PrintPool> printerPrintPoolList, String printPort, String printEncoding) {
        this.printerJob = printerJob;
        this.printerPrintPoolList.addAll(printerPrintPoolList);
        this.printPort = printPort;
        this.printEncoding = printEncoding;
    }

    @Override
    public void run() {
        printerJob.print(printerPrintPoolList, printPort, printEncoding);
    }
}
