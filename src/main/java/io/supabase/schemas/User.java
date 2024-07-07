package io.supabase.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class User {
    String id;

    @JsonProperty("aud")
    String aud;

    @JsonProperty("role")
    String role;

    @JsonProperty("email")
    String email;

    @JsonProperty("email_confirmed_at")
    String emailConfirmedAt;

    @JsonProperty("phone")
    String phone;

    @JsonProperty("phone_confirmed_at")
    String phoneConfirmedAt;

    @JsonProperty("confirmation_sent_at")
    String confirmationSentAt;

    @JsonProperty("confirmed_at")
    String confirmedAt;

    @JsonProperty("recovery_sent_at")
    String recoverySentAt;

    @JsonProperty("new_email")
    String newEmail;

    @JsonProperty("email_change_sent_at")
    String emailChangeSentAt;

    @JsonProperty("new_phone")
    String newPhone;

    @JsonProperty("phone_change_sent_at")
    String phoneChangeSentAt;

    @JsonProperty("reauthentication_sent_at")
    String reauthenticationSentAt;

    @JsonProperty("last_sign_in_at")
    String lastSignInAt;

    @JsonProperty("app_metadata")
    Map<String, Object> appMetadata = new HashMap<>();

    @JsonProperty("user_metadata")
    Map<String, Object> userMetadata = new HashMap<>();

    @JsonProperty("factors")
    List<MFAFactorSchema> factors;

    @JsonProperty("identities")
    List<UserIdentity> identities;

    @JsonProperty("banned_until")
    String bannedUntil;

    @JsonProperty("created_at")
    String createdAt;

    @JsonProperty("updated_at")
    String updatedAt;

    @JsonProperty("deleted_at")
    String deletedAt;

    @JsonProperty("is_anonymous")
    boolean isAnonymous;
}
