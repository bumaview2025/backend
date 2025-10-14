package bumaview.bumaview.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class PdfToMarkdownConverter {

    public boolean isPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        return "application/pdf".equals(contentType);
    }

    public String convertPdfToMarkdown(MultipartFile file) {
        try {
            PDDocument document = Loader.loadPDF(file.getBytes());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            
            return formatToMarkdown(text);
        } catch (IOException e) {
            log.error("Failed to convert PDF to Markdown", e);
            throw new RuntimeException("PDF 변환 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String formatToMarkdown(String text) {
        return text
                .replaceAll("\\n\\s*\\n", "\n\n")
                .replaceAll("(?m)^([A-Z][^\\n]*):$", "## $1")
                .trim();
    }
}