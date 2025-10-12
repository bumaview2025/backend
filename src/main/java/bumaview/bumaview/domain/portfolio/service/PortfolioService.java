package bumaview.bumaview.domain.portfolio.service;

import bumaview.bumaview.domain.user.domain.entity.UserEntity;
import bumaview.bumaview.domain.user.infra.repository.UserRepository;
import bumaview.bumaview.global.exception.BumaviewException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final UserRepository userRepository;
    private final PdfToMarkdownConverter pdfConverter;

    @Transactional
    public void uploadPortfolio(Long userId, MultipartFile file) {
        // Validate file
        if (file == null || file.isEmpty()) {
            throw new BumaviewException(HttpStatus.BAD_REQUEST, "파일이 비어있습니다.");
        }

        // Check if PDF file
        if (!pdfConverter.isPdfFile(file)) {
            throw new BumaviewException(HttpStatus.BAD_REQUEST, "해당 파일의 형식이 올바르지 않습니다.");
        }

        // Get user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BumaviewException(HttpStatus.UNAUTHORIZED, "당신은 이 프로그램의 사용자가 아닙니다."));

        // Convert PDF to Markdown
        String markdownContent = pdfConverter.convertPdfToMarkdown(file);

        // Save to user entity
        user.updateProfile(null, null, null, null, markdownContent, null);

        log.info("Portfolio uploaded successfully for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public String getPortfolio(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BumaviewException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        return user.getPortfolio();
    }
}