package io.supabase.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserSchema {
    private String id;

    @JsonProperty("aud")
    private String aud;

    @JsonProperty("role")
    private String role;

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_confirmed_at")
    private String emailConfirmedAt;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("phone_confirmed_at")
    private String phoneConfirmedAt;

    @JsonProperty("confirmation_sent_at")
    private String confirmationSentAt;

    @JsonProperty("confirmed_at")
    private String confirmedAt;

    @JsonProperty("recovery_sent_at")
    private String recoverySentAt;

    @JsonProperty("new_email")
    private String newEmail;

    @JsonProperty("email_change_sent_at")
    private String emailChangeSentAt;

    @JsonProperty("new_phone")
    private String newPhone;

    @JsonProperty("phone_change_sent_at")
    private String phoneChangeSentAt;

    @JsonProperty("reauthentication_sent_at")
    private String reauthenticationSentAt;

    @JsonProperty("last_sign_in_at")
    private String lastSignInAt;

    @JsonProperty("app_metadata")
    private Object appMetadata;

    @JsonProperty("user_metadata")
    private Object userMetadata;

    @JsonProperty("factors")
    private List<MFAFactorSchema> factors;

    @JsonProperty("identities")
    private List<IdentitySchema> identities;

    @JsonProperty("banned_until")
    private String bannedUntil;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("deleted_at")
    private String deletedAt;

    @JsonProperty("is_anonymous")
    private boolean isAnonymous;
}
