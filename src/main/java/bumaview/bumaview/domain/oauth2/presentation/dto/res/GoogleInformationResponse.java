package bumaview.bumaview.domain.oauth2.presentation.dto.res;

public record GoogleInformationResponse(
        String sub,
        String name,
        String picture
) {}
