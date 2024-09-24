package hampad;



import java.awt.print.*;
import java.awt.*;

public class PadPrinter implements Printable {

    public int print(Graphics g, PageFormat pf, int page)
            throws PrinterException {

        // We have only one page, and 'page'
        // is zero-based
        if (page > 0) {
            System.out.println("bo such page");
            return NO_SUCH_PAGE;
        }

        // User (0,0) is typically outside the
        // imageable area, so we must translate
        // by the X and Y values in the PageFormat
        // to avoid clipping.
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        g.drawString("hellp" ,50,50);
        // tell the caller that this page is part
        // of the printed document
        return PAGE_EXISTS;
    }

    public void doPrint() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
                /* The job did not successfully complete */
            }
        }
    }
}