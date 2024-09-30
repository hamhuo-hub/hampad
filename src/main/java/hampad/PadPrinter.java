// Package declaration for the class
package hampad;

// Import necessary classes for printing
import java.awt.print.*;
import java.awt.*;

// PadPrinter class implements Printable interface, defining how the contents should be printed
public class PadPrinter implements Printable {

    //factory method to create a new PadPrinter
    public static PadPrinter newPadPrinter(){
        return new PadPrinter();
    }

    // Method that gets called to render a page for printing
    public int print(Graphics g, PageFormat pf, int page)
            throws PrinterException {

        // Check if the requested page is beyond the total number of pages we have to print (in this case, 1)
        if (page > 0) {
            System.out.println("No such page");
            // Return NO_SUCH_PAGE to indicate that the requested page number is not available
            return NO_SUCH_PAGE;
        }

        // The coordinates (0,0) are outside the printable area of the page,
        // so we need to translate the origin to the printable area of the page specified by the PageFormat
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Draw the string "sample" at position (50, 50) within the translated region
        g.drawString("sample", 50, 50);

        // Return PAGE_EXISTS to indicate that the page was rendered successfully and is part of the document
        return PAGE_EXISTS;
    }



    // Method to initiate the printing process
    public void doPrint() {
        // Obtain a PrinterJob object to manage the printing process
        PrinterJob job = PrinterJob.getPrinterJob();
        // Set the object to be printed as this, as we're implementing the Printable interface
        job.setPrintable(this);
        // Display a print dialog to the user; returns true if the user decides to proceed
        boolean ok = job.printDialog();
        if (ok) {
            try {
                // Proceed with printing since user accepted the print job
                job.print();
            } catch (PrinterException ex) {
                // Handle the error scenario where the job did not successfully complete
                ex.printStackTrace();
            }
        }
    }
}
