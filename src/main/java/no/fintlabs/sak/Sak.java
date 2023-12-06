package no.fintlabs.sak;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class Sak {
    @NotNull
    private final String id;
    @NotNull
    private final String felt1;
    @NotNull
    private final String felt2;
    @NotNull
    private final String felt3;

    private final List<@Valid @NotNull SakObject> sakObjekter;
}
