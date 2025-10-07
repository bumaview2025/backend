package bumaview.bumaview.global.exception;

import bumaview.bumaview.global.security.jwt.exception.InvalidJsonWebTokenException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidJsonWebTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJsonWebTokenException(InvalidJsonWebTokenException e) {
        log.warn("JWT token validation failed: {}", e.getMessage());
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "INVALID_JWT_TOKEN");
        response.put("message", "인증 토큰이 유효하지 않습니다.");
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(BumaviewException.class)
    public ResponseEntity<Map<String, Object>> handleBumaviewException(BumaviewException e) {
        log.error("BumaviewException occurred: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "APPLICATION_ERROR");
        response.put("message", e.getMessage());
        response.put("status", e.getStatus().value());
        
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException e) {
        log.error("FeignException occurred: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "EXTERNAL_API_ERROR");
        response.put("message", "외부 API 호출 중 오류가 발생했습니다.");
        response.put("status", HttpStatus.BAD_GATEWAY.value());
        
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException occurred: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "MISSING_PARAMETER");
        response.put("message", "필수 파라미터가 누락되었습니다: " + e.getParameterName());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDataAccessResourceUsageException(InvalidDataAccessResourceUsageException e) {
        log.error("InvalidDataAccessResourceUsageException occurred: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "DATABASE_ERROR");
        response.put("message", "데이터베이스 오류가 발생했습니다.");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        response.put("error", "INTERNAL_SERVER_ERROR");
        response.put("message", "서버 내부 오류가 발생했습니다.");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}