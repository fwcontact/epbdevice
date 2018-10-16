package com.epb.epbdevice.test;

import com.epb.epbdevice.Epbdevice;
import com.epb.epbdevice.Epbprinter;

public class Test {
	public static void main(String args[]) {
		// initial COM port when open POS
        Epbdevice.initBat("D:\\EPBrowser\\EPBSH\\Shell\\init.bat");
        
        // printer
//        Epbprinter.OpenEpbprinter("D:\\test.txt");
        Epbprinter.OpenEpbprinter("COM2");
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
        Epbprinter.openDrawer("COM2", null);
    }
}
