package no.fintlabs;

import no.fintlabs.gateway.instance.InstanceProcessor;
import no.fintlabs.gateway.instance.InstanceProcessorFactoryService;
import no.fintlabs.sak.SakInstance;
import no.fintlabs.sak.SakInstanceMappingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class InstanceProcessorConfiguration {

    @Bean
    public InstanceProcessor<SakInstance> sakInstanceProcessor(
            InstanceProcessorFactoryService instanceProcessorFactoryService,
            SakInstanceMappingService sakInstanceMappingService
    ) {
        return instanceProcessorFactoryService.createInstanceProcessor(
                "sak",
                sakInstance -> Optional.ofNullable(sakInstance.getId()),
                sakInstanceMappingService
        );
    }

//    @Bean
//    public InstanceProcessor<EgrunnervervJournalpostInstance> journalpostInstanceProcessor(
//            InstanceProcessorFactoryService instanceProcessorFactoryService,
//            EgrunnervervJournalpostInstanceMappingService egrunnervervJournalpostInstanceMappingService
//    ) {
//        return instanceProcessorFactoryService.createInstanceProcessor(
//                "journalpost",
//                egrunnervervJournalpostInstance -> Optional.ofNullable(
//                        egrunnervervJournalpostInstance.getEgrunnervervJournalpostInstanceBody().getSysId()
//                ),
//                egrunnervervJournalpostInstanceMappingService
//        );
//    }


}
