package no.novari.instance.gateway.example;

import no.novari.flyt.instance.gateway.InstanceProcessor;
import no.novari.flyt.instance.gateway.InstanceProcessorFactoryService;
import no.novari.instance.gateway.example.collectionandfiles.IncomingInstanceWithCollectionOfFiles;
import no.novari.instance.gateway.example.collectionandfiles.IncomingInstanceWithCollectionOfFilesMappingService;
import no.novari.instance.gateway.example.simple.IncomingInstance;
import no.novari.instance.gateway.example.simple.IncomingInstanceMappingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class InstanceProcessorConfiguration {

    @Bean
    public InstanceProcessor<IncomingInstance> simpleInstanceProcessor(
            InstanceProcessorFactoryService instanceProcessorFactoryService,
            IncomingInstanceMappingService incomingInstanceMappingService
    ) {
        return instanceProcessorFactoryService.createInstanceProcessor(
                "instance",
                incomingInstance -> Optional.of(incomingInstance.getStringValue1()),
                incomingInstanceMappingService
        );
    }

    @Bean
    public InstanceProcessor<IncomingInstanceWithCollectionOfFiles> instanceWithCollectionAndFilesProcessor(
            InstanceProcessorFactoryService instanceProcessorFactoryService,
            IncomingInstanceWithCollectionOfFilesMappingService incomingInstanceWithCollectionOfFilesMappingService
    ) {
        return instanceProcessorFactoryService.createInstanceProcessor(
                "instanceWithCollectionAndFiles",
                incomingInstanceWithCollectionAndFiles -> Optional.ofNullable(
                        incomingInstanceWithCollectionAndFiles.getStringValue2()
                ),
                incomingInstanceWithCollectionOfFilesMappingService
        );
    }


}
