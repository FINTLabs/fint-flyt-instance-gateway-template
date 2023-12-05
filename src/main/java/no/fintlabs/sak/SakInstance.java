package no.fintlabs.sak;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class SakInstance {
    @NotNull
    private final String id;
    @NotNull
    private final String felt1;
    @NotNull
    private final String felt2;
    @NotNull
    private final String felt3;

    private final List<@Valid @NotNull SakSamling> samling;
}
