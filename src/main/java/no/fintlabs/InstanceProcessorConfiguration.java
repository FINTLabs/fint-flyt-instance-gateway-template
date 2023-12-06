package no.fintlabs;

import no.fintlabs.gateway.instance.InstanceProcessor;
import no.fintlabs.gateway.instance.InstanceProcessorFactoryService;
import no.fintlabs.journalpost.Journalpost;
import no.fintlabs.journalpost.JournalpostMappingService;
import no.fintlabs.sak.Sak;
import no.fintlabs.sak.SakMappingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class InstanceProcessorConfiguration {

    @Bean
    public InstanceProcessor<Sak> sakInstanceProcessor(
            InstanceProcessorFactoryService instanceProcessorFactoryService,
            SakMappingService sakMappingService
    ) {
        return instanceProcessorFactoryService.createInstanceProcessor(
                "sak",
                sak -> Optional.ofNullable(sak.getId()),
                sakMappingService
        );
    }

    @Bean
    public InstanceProcessor<Journalpost> journalpostInstanceProcessor(
            InstanceProcessorFactoryService instanceProcessorFactoryService,
            JournalpostMappingService egrunnervervJournalpostMappingService
    ) {
        return instanceProcessorFactoryService.createInstanceProcessor(
                "journalpost",
                egrunnervervJournalpost -> Optional.ofNullable(
                        egrunnervervJournalpost.getId()
                ),
                egrunnervervJournalpostMappingService
        );
    }


}
