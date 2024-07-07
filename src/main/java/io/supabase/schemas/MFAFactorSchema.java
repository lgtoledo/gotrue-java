package io.supabase.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MFAFactorSchema {
    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("friendly_name")
    private String friendlyName;

    @JsonProperty("factor_type")
    private String factorType;
}
