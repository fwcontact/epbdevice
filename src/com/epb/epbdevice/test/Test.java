package com.epb.epbdevice.test;

import com.epb.epbdevice.Epbdevice;
import com.epb.epbdevice.Epbnetprinter;
import com.epb.epbdevice.Epbprinter;

public class Test {

    public static void main(String args[]) {
        // initial COM port when open POS
        Epbdevice.initBat("D:\\EPBrowser\\EPBSH\\Shell\\init.bat");

        String printPort = "COM2";
//        String printPort = "192.168.0.201";
        if (printPort != null && (printPort.toUpperCase().startsWith("COM") || printPort.toUpperCase().startsWith("LPT"))) { // printPort = "COM2"
            // printer
            Epbprinter.OpenEpbprinter(printPort);
            Epbprinter.printText("          Test ORDER          ");
            Epbprinter.printText("Casher    :Admin");
            Epbprinter.printText("Print Time:2018/10/15");
            Epbprinter.printText("No      Stock ID      Qty      Amount");
            Epbprinter.printText("1       A001          1        20.00");
            Epbprinter.printText("2       A002          1        50.00");
            Epbprinter.printText("                     Pay Money:70.00");
            Epbprinter.cutPaper();
            Epbprinter.closePrinter();

            // open drawer
            Epbprinter.openDrawer(printPort, null);
        } else if (printPort != null && Epbnetprinter.checkNetPort(printPort)) { // printPort = "192.168.0.201"
            // net printer
            // net printer not support open drawer
            if (Epbnetprinter.openEpbNetPrinter(printPort)) {
                Epbnetprinter.printText("          Test ORDER          ");
                Epbnetprinter.printText("Casher    :Admin");
                Epbnetprinter.printText("Print Time:2018/10/15");
                Epbnetprinter.printText("No      Stock ID      Qty      Amount");
                Epbnetprinter.printText("1       A001          1        20.00");
                Epbnetprinter.printText("2       A002          1        50.00");
                Epbnetprinter.printText("                     Pay Money:70.00");
                Epbnetprinter.cutPaper();
                Epbnetprinter.closeNetPrinter();
            }
        }
    }
}
