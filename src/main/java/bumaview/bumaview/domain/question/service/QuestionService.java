package bumaview.bumaview.domain.question.service;

import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import bumaview.bumaview.domain.question.domain.repository.QuestionRepository;
import bumaview.bumaview.domain.question.presentation.dto.req.QuestionCreateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.req.QuestionUpdateRequestDto;
import bumaview.bumaview.domain.question.presentation.dto.res.QuestionResponseDto;
import bumaview.bumaview.domain.question.exception.QuestionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<QuestionResponseDto> getAllQuestions() {
        return questionRepository.findAll()
                .stream()
                .map(QuestionResponseDto::from)
                .toList();
    }

    public List<QuestionResponseDto> searchQuestions(String column, String query) {
        return questionRepository.findByColumnAndQuery(column, query)
                .stream()
                .map(QuestionResponseDto::from)
                .toList();
    }

    @Transactional
    public Long createQuestion(QuestionCreateRequestDto request) {
        QuestionEntity question = QuestionEntity.builder()
                .question(request.question())
                .category(request.category())
                .company(request.company())
                .questionAt(request.questionAt())
                .build();

        return questionRepository.save(question).getId();
    }

    @Transactional
    public void updateQuestion(QuestionUpdateRequestDto request) {
         QuestionEntity question = questionRepository.findById(request.id())
                .orElseThrow(() -> new QuestionNotFoundException("해당 아이디 값이 존재하지 않습니다."));

         question.updateQuestion(request.question(), request.category(), request.company(), request.questionAt());

        questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new QuestionNotFoundException("해당 아이디 값이 존재하지 않습니다.");
        }
        questionRepository.deleteById(id);
    }

    @Transactional
    public void uploadQuestionFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일이 비어있습니다.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new IllegalArgumentException("Excel 파일만 업로드 가능합니다. (.xlsx, .xls)");
        }

        try {
            List<QuestionEntity> questions = parseExcelFile(file);
            questionRepository.saveAll(questions);
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private List<QuestionEntity> parseExcelFile(MultipartFile file) throws IOException {
        List<QuestionEntity> questions = new ArrayList<>();
        
        Workbook workbook = null;
        try {
            String filename = file.getOriginalFilename();
            if (filename != null && filename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                workbook = new HSSFWorkbook(file.getInputStream());
            }

            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String question = getCellValueAsString(row.getCell(0));
                String category = getCellValueAsString(row.getCell(1));
                String company = getCellValueAsString(row.getCell(2));
                Integer questionAt = getCellValueAsInteger(row.getCell(3));

                if (question != null && !question.trim().isEmpty() &&
                    category != null && !category.trim().isEmpty() &&
                    company != null && !company.trim().isEmpty()) {
                    
                    QuestionEntity questionEntity = QuestionEntity.builder()
                            .question(question.trim())
                            .category(category.trim())
                            .company(company.trim())
                            .questionAt(questionAt)
                            .build();
                    
                    questions.add(questionEntity);
                }
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }

        return questions;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    public byte[] downloadSampleFile() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("면접질문 샘플");
            
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("질문");
            headerRow.createCell(1).setCellValue("카테고리");
            headerRow.createCell(2).setCellValue("회사명");
            headerRow.createCell(3).setCellValue("출제년도");
            
            Row sampleRow1 = sheet.createRow(1);
            sampleRow1.createCell(0).setCellValue("자바의 OOP 특징에 대해 설명해주세요.");
            sampleRow1.createCell(1).setCellValue("Java");
            sampleRow1.createCell(2).setCellValue("네이버");
            sampleRow1.createCell(3).setCellValue(2024);
            
            Row sampleRow2 = sheet.createRow(2);
            sampleRow2.createCell(0).setCellValue("Spring Boot의 자동 설정에 대해 설명해주세요.");
            sampleRow2.createCell(1).setCellValue("Spring");
            sampleRow2.createCell(2).setCellValue("카카오");
            sampleRow2.createCell(3).setCellValue(2024);
            
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new RuntimeException("샘플 파일 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}