package bumaview.bumaview.domain.portfolio.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class PdfToMarkdownConverter {

    public String convertPdfToMarkdown(MultipartFile pdfFile) {
        try (PDDocument document = Loader.loadPDF(pdfFile.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // Convert plain text to basic Markdown format
            String markdown = convertToMarkdown(text);

            log.info("Successfully converted PDF to Markdown: {} characters", markdown.length());
            return markdown;

        } catch (IOException e) {
            log.error("Failed to process PDF file", e);
            throw new RuntimeException("PDF 파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    private String convertToMarkdown(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder markdown = new StringBuilder();
        String[] lines = text.split("\n");

        for (String line : lines) {
            String trimmedLine = line.trim();

            if (trimmedLine.isEmpty()) {
                markdown.append("\n");
                continue;
            }

            // Check if line looks like a heading (all caps, or short line)
            if (trimmedLine.length() < 100 &&
                (trimmedLine.equals(trimmedLine.toUpperCase()) && trimmedLine.length() > 3) ||
                (trimmedLine.matches("^[0-9]+\\..+") || trimmedLine.matches("^[A-Z][a-z]+.*:$"))) {
                markdown.append("## ").append(trimmedLine).append("\n\n");
            } else {
                markdown.append(trimmedLine).append("\n");
            }
        }

        return markdown.toString();
    }

    public boolean isPdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        return (contentType != null && contentType.equals("application/pdf")) ||
               (filename != null && filename.toLowerCase().endsWith(".pdf"));
    }
}