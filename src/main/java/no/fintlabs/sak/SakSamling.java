package no.fintlabs.sak;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class SakSamling {
    @NotNull
    private final String felt1;
    @NotNull
    private final String felt2;
    @NotNull
    private final String felt3;
}