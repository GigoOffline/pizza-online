package de.stea1th.pdfcreator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PdfCreatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdfCreatorApplication.class, args);
//        ApplicationContext app = SpringApplication.run(PdfCreatorApplication.class, args);
//        PdfCreatorService creator = app.getBean(PdfCreatorService.class);
//        creator.createPdf();
    }

}
