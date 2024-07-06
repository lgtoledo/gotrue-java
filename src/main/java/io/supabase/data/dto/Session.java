package io.supabase.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.supabase.schemas.UserSchema;
import io.supabase.schemas.WeakPassword;
import lombok.Getter;
import lombok.Setter;

// AccessTokenResponseSchema

@Getter
@Setter
public class Session {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("expires_at")
    private int expiresAt;

    @JsonProperty("weak_password")
    private WeakPassword weakPassword;

    @JsonProperty("user")
    private UserSchema user;

}
