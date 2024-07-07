package io.supabase.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UserIdentity {
    @JsonProperty("identity_id")
    private String identityId;

    @JsonProperty("id")
    private String id;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("identity_data")
    private Map<String, Object> identityData = new HashMap<>();

    @JsonProperty("provider")
    private String provider;

    @JsonProperty("last_sign_in_at")
    private String lastSignInAt;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("email")
    private String email;
}
