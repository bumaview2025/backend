package bumaview.bumaview.domain.oauth2.presentation.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        Long expiresIn,

        @JsonProperty("token_type")
        String tokenType,

        String scope,

        @JsonProperty("refresh_token")
        String refreshToken
) {}

