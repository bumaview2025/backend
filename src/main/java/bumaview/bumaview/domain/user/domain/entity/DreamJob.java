package bumaview.bumaview.domain.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DreamJob {
    EMBEDDED("임베디드"),
    FRONTEND("프론트엔드"),
    BACKEND("백엔드"),
    INFRA("인프라"),
    AI("AI"),
    SECURITY("보안"),
    DESIGN("디자인"),
    PUBLIC_OFFICIAL("공무원/공기업");

    private final String value;

    @JsonCreator
    public static DreamJob from(String value) {
        for (DreamJob job : DreamJob.values()) {
            if (job.name().equalsIgnoreCase(value) || job.getValue().equals(value)) {
                return job;
            }
        }
        throw new IllegalArgumentException("Invalid DreamJob value: " + value);
    }
}