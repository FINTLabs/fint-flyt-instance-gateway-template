package no.fintlabs.journalpost;

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
public class Journalpost {
    @NotNull
    private final String id;
    @NotNull
    private final String felt1;
    @NotNull
    private final String felt2;
    @NotNull
    private final String felt3;
    @NotNull
    private final List<@Valid @NotNull JournalpostDokument> dokumenter;
}
