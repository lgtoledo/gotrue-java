package io.supabase.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Settings {

    @JsonProperty("disable_signup")
    Boolean disableSignup;

    @JsonProperty("mailer_autoconfirm")
    Boolean mailerAutoconfirm;

    @JsonProperty("phone_autoconfirm")
    Boolean phoneAutoconfirm;

    @JsonProperty("sms_provider")
    String smsProvider;

    @JsonProperty("mfa_enabled")
    Boolean mfaEnabled;

    @JsonProperty("saml_enabled")
    Boolean samlEnabled;

    @JsonProperty("external")
    Map<String, Boolean> external = new HashMap<>();
}
