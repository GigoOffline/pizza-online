package de.stea1th.pdfcreator;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;


@Component
public class PdfCreator {

    private InvoicePdfBlockCreator pdfBlockCreator;

    public PdfCreator(InvoicePdfBlockCreator pdfBlockCreator) {
        this.pdfBlockCreator = pdfBlockCreator;
    }

    @SneakyThrows
    public void createPdf() {
        try (PdfDocument pdf = new PdfDocument(new PdfWriter("pdf-creator\\src\\main\\resources\\pdf\\test.pdf", new WriterProperties().addXmpMetadata()));
             Document document = new Document(pdf, PageSize.A4)) {

            pdf.getCatalog().setLang(new PdfString("de-DE"));
            pdf.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));

            document.add(pdfBlockCreator.createHeaderCompanySignature());
            document.add(pdfBlockCreator.createCustomerDetails());
            document.add(pdfBlockCreator.createInvoiceDetailTable());
            document.add(pdfBlockCreator.createInvoiceNumber());
            document.add(pdfBlockCreator.createForeword());
            document.add(pdfBlockCreator.createInvoiceProductTable());
            document.add(pdfBlockCreator.createInvoiceTotalTable());
            document.add(pdfBlockCreator.createEpilogue());
            pdfBlockCreator.createFooter(pdf);
        }
    }
}
