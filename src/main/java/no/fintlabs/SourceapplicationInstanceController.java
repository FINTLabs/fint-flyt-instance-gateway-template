package no.fintlabs;

import lombok.extern.slf4j.Slf4j;
import no.fintlabs.gateway.instance.InstanceProcessor;
import no.fintlabs.journalpost.JournalpostInstance;
import no.fintlabs.sak.SakInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static no.fintlabs.resourceserver.UrlPaths.EXTERNAL_API;

@RestController
@RequestMapping(EXTERNAL_API + "/sourceapplication/instances/{orgNr}")
@Slf4j
public class SourceapplicationInstanceController {


    private final InstanceProcessor<SakInstance> sakInstanceProcessor;
//    private final InstanceProcessor<JournalpostInstance> journalpostInstanceProcessor;

    public SourceapplicationInstanceController(
            InstanceProcessor<SakInstance> sakInstanceProcessor
//            InstanceProcessor<JournalpostInstance> journalpostInstanceProcessor
    ) {
        this.sakInstanceProcessor = sakInstanceProcessor;
//        this.journalpostInstanceProcessor = journalpostInstanceProcessor;
    }


    @PostMapping("sak")
    public Mono<ResponseEntity<?>> postSakInstance(
            @RequestBody SakInstance sakInstance,
            @AuthenticationPrincipal Mono<Authentication> authenticationMono
    ) {
        return authenticationMono.flatMap(
                authentication -> sakInstanceProcessor.processInstance(
                        authentication,
                        sakInstance
                )
        );
    }

//    @PostMapping("journalpost")
//    public Mono<ResponseEntity<?>> postJournalpostInstance(
//            @RequestBody JournalpostInstance journalpostInstance,
//            @AuthenticationPrincipal Mono<Authentication> authenticationMono
//    ) {
//        return authenticationMono.flatMap(
//                authentication -> journalpostInstanceProcessor.processInstance(
//                        authentication,
//                        journalpostInstance
//                )
//        );
//    }

}