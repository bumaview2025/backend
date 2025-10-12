package bumaview.bumaview.domain.oauth2.presentation.dto.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleInformationResponse(
        String sub,
        String name,
        @JsonProperty("given_name")
        String givenName,
        @JsonProperty("family_name")
        String familyName,
        String picture,
        String email,
        @JsonProperty("email_verified")
        Boolean emailVerified,
        String locale
) {}
