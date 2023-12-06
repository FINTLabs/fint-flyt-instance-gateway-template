package no.fintlabs;

import no.fintlabs.gateway.instance.InstanceProcessor;
import no.fintlabs.journalpost.Journalpost;
import no.fintlabs.sak.Sak;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static no.fintlabs.resourceserver.UrlPaths.EXTERNAL_API;

@RestController
@RequestMapping(EXTERNAL_API + "/sourceapplication/instances")
public class InstanceController {

    private final InstanceProcessor<Sak> sakInstanceProcessor;
    private final InstanceProcessor<Journalpost> journalpostInstanceProcessor;

    public InstanceController(
            InstanceProcessor<Sak> sakInstanceProcessor,
            InstanceProcessor<Journalpost> journalpostInstanceProcessor
    ) {
        this.sakInstanceProcessor = sakInstanceProcessor;
        this.journalpostInstanceProcessor = journalpostInstanceProcessor;
    }


    @PostMapping("sak")
    public Mono<ResponseEntity<?>> postSakInstance(
            @RequestBody Sak sak,
            @AuthenticationPrincipal Mono<Authentication> authenticationMono
    ) {
        return authenticationMono.flatMap(
                authentication -> sakInstanceProcessor.processInstance(
                        authentication,
                        sak
                )
        );
    }

    @PostMapping("journalpost")
    public Mono<ResponseEntity<?>> postJournalpostInstance(
            @RequestBody Journalpost journalpost,
            @AuthenticationPrincipal Mono<Authentication> authenticationMono
    ) {
        return authenticationMono.flatMap(
                authentication -> journalpostInstanceProcessor.processInstance(
                        authentication,
                        journalpost
                )
        );
    }

}
