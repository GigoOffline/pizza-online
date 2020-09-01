package de.gigo.pdfcreator.service;

import com.lowagie.text.DocumentException;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class GeneratePDF
{
    public String parseThymeleafTemplate(Context variableContext, String template)
    {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
//        https://stackoverflow.com/questions/20523245/spring-thymeleaf-and-localized-strings
//        templateEngine.setMessageResolver();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process(template, variableContext);
    }

    public String generatePdfFromHtml(String html, String idno) throws IOException, DocumentException
    {
        String outputFolder = System.getProperty("user.home") + File.separator + idno + ".pdf";
        OutputStream outputStream = new FileOutputStream(outputFolder);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
        return outputFolder;
    }
}
