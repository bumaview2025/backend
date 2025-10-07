package bumaview.bumaview.global.service;

import bumaview.bumaview.global.properties.FileUploadProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileUploadProperties fileUploadProperties;

    public String uploadPortfolioFile(MultipartFile file, String userId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("파일명이 비어있습니다.");
        }

        String fileExtension = getFileExtension(originalFilename);
        String newFileName = "portfolio_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
        
        Path uploadPath = Paths.get(fileUploadProperties.getPortfolioDir());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }

    public Resource downloadFile(String filename, String directory) throws IOException {
        Path filePath = Paths.get(directory).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("파일을 찾을 수 없습니다: " + filename);
        }
    }

    public Resource downloadSampleFile() throws IOException {
        String sampleFileName = createSampleFile();
        return downloadFile(sampleFileName, fileUploadProperties.getSampleDir());
    }

    public String createSampleFile() throws IOException {
        String sampleContent = """
                포트폴리오 샘플 파일
                
                1. 개인 정보
                - 이름: 홍길동
                - 연락처: 010-1234-5678
                - 이메일: hong@example.com
                
                2. 기술 스택
                - Backend: Java, Spring Boot, JPA
                - Frontend: React, JavaScript, HTML/CSS
                - Database: PostgreSQL, MySQL
                - Tools: Git, Docker, AWS
                
                3. 프로젝트 경험
                가. 부마뷰 프로젝트 (2024.01 ~ 2024.12)
                - 면접 질문 관리 시스템 개발
                - Spring Boot, PostgreSQL 사용
                - OAuth2 구글 로그인 구현
                - RESTful API 설계 및 개발
                
                나. 쇼핑몰 프로젝트 (2023.06 ~ 2023.12)
                - 온라인 쇼핑몰 백엔드 개발
                - 결제 시스템 연동
                - 상품 관리 및 주문 처리 기능
                
                4. 자격증
                - 정보처리기사
                - SQLD
                
                5. 자기소개
                안녕하세요. 백엔드 개발자를 꿈꾸는 신입 개발자입니다.
                사용자 중심의 서비스를 개발하며, 클린 코드와 효율적인 시스템 설계에 관심이 많습니다.
                팀워크를 중시하며 지속적인 학습을 통해 성장하는 개발자가 되겠습니다.
                """;

        Path sampleDir = Paths.get(fileUploadProperties.getSampleDir());
        if (!Files.exists(sampleDir)) {
            Files.createDirectories(sampleDir);
        }

        String sampleFileName = "portfolio_sample_" + UUID.randomUUID().toString() + ".txt";
        Path sampleFilePath = sampleDir.resolve(sampleFileName);
        Files.write(sampleFilePath, sampleContent.getBytes());

        return sampleFileName;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex >= 0) {
            return filename.substring(dotIndex);
        }
        return "";
    }
}