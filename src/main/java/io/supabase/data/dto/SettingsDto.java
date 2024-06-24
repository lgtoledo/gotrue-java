package io.supabase.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
public class SettingsDto {
    @JsonProperty("external")
    Map<String, Boolean> external;
    @JsonProperty("disable_signup")
    Boolean disableSignup;
    @JsonProperty("mailer_autoconfirm")
    Boolean autoconfirm;
}
