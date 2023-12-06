package no.fintlabs.journalpost;

import no.fintlabs.gateway.instance.InstanceMapper;
import no.fintlabs.gateway.instance.model.File;
import no.fintlabs.gateway.instance.model.instance.InstanceObject;
import no.fintlabs.gateway.instance.web.FileClient;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.*;

@Service
public class JournalpostInstanceMappingService implements InstanceMapper<JournalpostInstance> {

    private final FileClient fileClient;

    public JournalpostInstanceMappingService(
            FileClient fileClient
    ) {
        this.fileClient = fileClient;
    }

    @Override
    public Mono<InstanceObject> map(Long sourceApplicationId, JournalpostInstance journalpostInstance) {

        JournalpostDocument hoveddokument = journalpostInstance
                .getDokumenter()
                .stream()
                .filter(JournalpostDocument::getHoveddokument)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hoveddokument"));

        List<JournalpostDocument> vedlegg = journalpostInstance
                .getDokumenter()
                .stream()
                .filter(dokument -> !dokument.getHoveddokument())
                .toList();

        Mono<Map<String, String>> hoveddokumentInstanceValuePerKeyMono = mapHoveddokumentToInstanceValuePerKey(
                sourceApplicationId,
                journalpostInstance.getId(),
                hoveddokument
        );
        Mono<List<InstanceObject>> vedleggInstanceObjectsMono = mapAttachmentDocumentsToInstanceObjects(
                sourceApplicationId,
                journalpostInstance.getId(),
                vedlegg
        );

        return Mono.zip(
                        hoveddokumentInstanceValuePerKeyMono,
                        vedleggInstanceObjectsMono
                )
                .map((Tuple2<Map<String, String>, List<InstanceObject>> hovedDokumentValuePerKeyAndVedleggInstanceObjects) -> {

                            HashMap<String, String> valuePerKey = getStringStringHashMap(journalpostInstance);

                            valuePerKey.putAll(hovedDokumentValuePerKeyAndVedleggInstanceObjects.getT1());
                            return InstanceObject.builder()
                                    .valuePerKey(valuePerKey)
                                    .objectCollectionPerKey(
                                            Map.of(
                                                    "vedlegg", hovedDokumentValuePerKeyAndVedleggInstanceObjects.getT2()
                                            ))
                                    .build();
                        }

                );
    }

    private static HashMap<String, String> getStringStringHashMap(JournalpostInstance journalpostInstance) {
        HashMap<String, String> valuePerKey = new HashMap<>();
        valuePerKey.put("id", Optional.ofNullable(journalpostInstance.getId()).orElse(""));
        valuePerKey.put("felt1", Optional.ofNullable(journalpostInstance.getFelt1()).orElse(""));
        valuePerKey.put("felt2", Optional.ofNullable(journalpostInstance.getFelt2()).orElse(""));
        valuePerKey.put("felt3", Optional.ofNullable(journalpostInstance.getFelt3()).orElse(""));
        return valuePerKey;
    }

    private Mono<List<InstanceObject>> mapAttachmentDocumentsToInstanceObjects(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            List<JournalpostDocument> journalpostDocuments
    ) {
        return Flux.fromIterable(journalpostDocuments)
                .flatMap(journalpostDocument -> mapAttachmentDocumentToInstanceObject(
                        sourceApplicationId, sourceApplicationInstanceId, journalpostDocument
                ))
                .collectList();
    }

    private MediaType getMediaType(JournalpostDocument journalpostDocument) {
        return MediaTypeFactory.getMediaType(journalpostDocument.getFilnavn())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No media type found for fileName=" + journalpostDocument.getFilnavn()
                ));
    }

    private Mono<Map<String, String>> mapHoveddokumentToInstanceValuePerKey(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            JournalpostDocument journalpostDocument
    ) {
        MediaType mediaType = getMediaType(journalpostDocument);
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                journalpostDocument,
                mediaType
        );
        return fileClient.postFile(file)
                .map(fileId -> mapHoveddokumentAndFileIdToInstanceValuePerKey(journalpostDocument, mediaType, fileId));
    }

    private Map<String, String> mapHoveddokumentAndFileIdToInstanceValuePerKey(
            JournalpostDocument journalpostDocument,
            MediaType mediaType,
            UUID fileId
    ) {
        return Map.of(
                "hoveddokumentTittel", Optional.ofNullable(journalpostDocument.getTittel()).orElse(""),
                "hoveddokumentFilnavn", Optional.ofNullable(journalpostDocument.getFilnavn()).orElse(""),
                "hoveddokumentMediatype", mediaType.toString(),
                "hoveddokumentFil", fileId.toString()
        );
    }

    private Mono<InstanceObject> mapAttachmentDocumentToInstanceObject(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            JournalpostDocument journalpostDocument
    ) {
        MediaType mediaType = getMediaType(journalpostDocument);
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                journalpostDocument,
                mediaType
        );
        return fileClient.postFile(file)
                .map(fileId -> mapAttachmentDocumentAndFileIdToInstanceObject(
                        journalpostDocument,
                        mediaType,
                        fileId));
    }

    private InstanceObject mapAttachmentDocumentAndFileIdToInstanceObject(
            JournalpostDocument journalpostDocument,
            MediaType mediaType,
            UUID fileId
    ) {

        return InstanceObject
                .builder()
                .valuePerKey(Map.of(
                        "tittel", Optional.ofNullable(journalpostDocument.getTittel()).orElse(""),
                        "filnavn", Optional.ofNullable(journalpostDocument.getFilnavn()).orElse(""),
                        "mediatype", mediaType.toString(),
                        "fil", fileId.toString()
                ))
                .build();
    }

    private File toFile(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            JournalpostDocument journalpostDocument,
            MediaType type
    ) {
        return File
                .builder()
                .name(journalpostDocument.getFilnavn())
                .type(type)
                .sourceApplicationId(sourceApplicationId)
                .sourceApplicationInstanceId(sourceApplicationInstanceId)
                .encoding("UTF-8")
                .base64Contents(journalpostDocument.getDokumentBase64())
                .build();
    }

}
