package no.fintlabs.sak;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class SakObject {
    @NotNull
    private final String felt1;
    @NotNull
    private final String felt2;
    @NotNull
    private final String felt3;
}