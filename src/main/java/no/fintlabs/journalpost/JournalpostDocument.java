package no.fintlabs.journalpost;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@Jacksonized
@EqualsAndHashCode
@Builder
public class JournalpostDocument {
    @NotNull
    private final String tittel;
    @NotNull
    private final Boolean hoveddokument;
    @NotNull
    private final String filnavn;
    @NotNull
    private final String dokumentBase64;
}
