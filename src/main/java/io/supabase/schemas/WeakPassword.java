package io.supabase.schemas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class WeakPassword {
    @JsonProperty("reasons")
    private List<String> reasons;

    @JsonProperty("message")
    private String message;
}
