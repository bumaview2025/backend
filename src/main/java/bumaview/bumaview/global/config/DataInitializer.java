package bumaview.bumaview.global.config;

import bumaview.bumaview.domain.question.domain.entity.QuestionEntity;
import bumaview.bumaview.domain.question.domain.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final QuestionRepository questionRepository;
    private static final String EXCEL_FILE_PATH = "/Users/lee/IdeaProjects/backend/2023to2025 interviews.CSV의 사본.xlsx";
    private static final String MARKER_FILE_PATH = "/Users/lee/IdeaProjects/backend/.data-initialized";

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // 이미 실행되었는지 체크
        File markerFile = new File(MARKER_FILE_PATH);
        if (markerFile.exists()) {
            log.info("데이터 초기화가 이미 완료되었습니다. 스킵합니다.");
            return;
        }

        File excelFile = new File(EXCEL_FILE_PATH);
        if (!excelFile.exists()) {
            log.warn("초기 데이터 파일이 존재하지 않습니다: {}", EXCEL_FILE_PATH);
            return;
        }

        try {
            log.info("면접 질문 데이터 초기화를 시작합니다...");
            List<QuestionEntity> questions = parseExcelFile(excelFile);
            questionRepository.saveAll(questions);

            // 마커 파일 생성 (완료 표시)
            markerFile.createNewFile();

            log.info("총 {}개의 면접 질문이 데이터베이스에 저장되었습니다.", questions.size());
            log.info("데이터 초기화가 완료되었습니다. 이 작업은 다시 실행되지 않습니다.");
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    private List<QuestionEntity> parseExcelFile(File file) throws IOException {
        List<QuestionEntity> questions = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

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
}