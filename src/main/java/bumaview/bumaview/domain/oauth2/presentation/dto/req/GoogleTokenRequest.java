package bumaview.bumaview.domain.oauth2.presentation.dto.req;

import feign.form.FormProperty;

public record GoogleTokenRequest(
    String code,

    @FormProperty("client_id")
    String clientId,

    @FormProperty("client_secret")
    String clientSecret,

    @FormProperty("redirect_uri")
    String redirectUri,

    @FormProperty("grant_type")
    String grantType
) {}
