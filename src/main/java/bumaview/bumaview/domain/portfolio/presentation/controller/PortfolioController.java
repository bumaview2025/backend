package bumaview.bumaview.domain.portfolio.presentation.controller;

import bumaview.bumaview.domain.portfolio.service.PdfToMarkdownConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PdfToMarkdownConverter pdfConverter;

    @PostMapping("/upload-pdf-md")
    public ResponseEntity<Resource> uploadPdfAndReturnMd(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!pdfConverter.isPdfFile(file)) {
            return ResponseEntity.badRequest().build();
        }
        String markdownContent = pdfConverter.convertPdfToMarkdown(file);
        ByteArrayResource resource = new ByteArrayResource(markdownContent.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=converted.md")
            .contentType(MediaType.parseMediaType("text/markdown"))
            .body(resource);
    }
}

