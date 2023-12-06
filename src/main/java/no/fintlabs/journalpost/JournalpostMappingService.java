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
public class JournalpostMappingService implements InstanceMapper<Journalpost> {

    private final FileClient fileClient;

    public JournalpostMappingService(
            FileClient fileClient
    ) {
        this.fileClient = fileClient;
    }

    @Override
    public Mono<InstanceObject> map(Long sourceApplicationId, Journalpost journalpost) {

        JournalpostDokument hoveddokument = journalpost
                .getDokumenter()
                .stream()
                .filter(JournalpostDokument::getHoveddokument)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No hoveddokument"));

        List<JournalpostDokument> vedlegg = journalpost
                .getDokumenter()
                .stream()
                .filter(dokument -> !dokument.getHoveddokument())
                .toList();

        Mono<Map<String, String>> hoveddokumentInstanceValuePerKeyMono = mapHoveddokumentToInstanceValuePerKey(
                sourceApplicationId,
                journalpost.getId(),
                hoveddokument
        );
        Mono<List<InstanceObject>> vedleggInstanceObjectsMono = mapAttachmentDocumentsToInstanceObjects(
                sourceApplicationId,
                journalpost.getId(),
                vedlegg
        );

        return Mono.zip(
                        hoveddokumentInstanceValuePerKeyMono,
                        vedleggInstanceObjectsMono
                )
                .map((Tuple2<Map<String, String>, List<InstanceObject>> hovedDokumentValuePerKeyAndVedleggInstanceObjects) -> {

                            HashMap<String, String> valuePerKey = getStringStringHashMap(journalpost);

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

    private static HashMap<String, String> getStringStringHashMap(Journalpost journalpost) {
        HashMap<String, String> valuePerKey = new HashMap<>();
        valuePerKey.put("id", Optional.ofNullable(journalpost.getId()).orElse(""));
        valuePerKey.put("felt1", Optional.ofNullable(journalpost.getFelt1()).orElse(""));
        valuePerKey.put("felt2", Optional.ofNullable(journalpost.getFelt2()).orElse(""));
        valuePerKey.put("felt3", Optional.ofNullable(journalpost.getFelt3()).orElse(""));
        return valuePerKey;
    }

    private Mono<List<InstanceObject>> mapAttachmentDocumentsToInstanceObjects(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            List<JournalpostDokument> journalpostDokuments
    ) {
        return Flux.fromIterable(journalpostDokuments)
                .flatMap(journalpostDokument -> mapAttachmentDocumentToInstanceObject(
                        sourceApplicationId, sourceApplicationInstanceId, journalpostDokument
                ))
                .collectList();
    }

    private MediaType getMediaType(JournalpostDokument journalpostDokument) {
        return MediaTypeFactory.getMediaType(journalpostDokument.getFilnavn())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No media type found for fileName=" + journalpostDokument.getFilnavn()
                ));
    }

    private Mono<Map<String, String>> mapHoveddokumentToInstanceValuePerKey(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            JournalpostDokument journalpostDokument
    ) {
        MediaType mediaType = getMediaType(journalpostDokument);
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                journalpostDokument,
                mediaType
        );
        return fileClient.postFile(file)
                .map(fileId -> mapHoveddokumentAndFileIdToInstanceValuePerKey(journalpostDokument, mediaType, fileId));
    }

    private Map<String, String> mapHoveddokumentAndFileIdToInstanceValuePerKey(
            JournalpostDokument journalpostDokument,
            MediaType mediaType,
            UUID fileId
    ) {
        return Map.of(
                "hoveddokumentTittel", Optional.ofNullable(journalpostDokument.getTittel()).orElse(""),
                "hoveddokumentFilnavn", Optional.ofNullable(journalpostDokument.getFilnavn()).orElse(""),
                "hoveddokumentMediatype", mediaType.toString(),
                "hoveddokumentFil", fileId.toString()
        );
    }

    private Mono<InstanceObject> mapAttachmentDocumentToInstanceObject(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            JournalpostDokument journalpostDokument
    ) {
        MediaType mediaType = getMediaType(journalpostDokument);
        File file = toFile(
                sourceApplicationId,
                sourceApplicationInstanceId,
                journalpostDokument,
                mediaType
        );
        return fileClient.postFile(file)
                .map(fileId -> mapAttachmentDocumentAndFileIdToInstanceObject(
                        journalpostDokument,
                        mediaType,
                        fileId));
    }

    private InstanceObject mapAttachmentDocumentAndFileIdToInstanceObject(
            JournalpostDokument journalpostDokument,
            MediaType mediaType,
            UUID fileId
    ) {

        return InstanceObject
                .builder()
                .valuePerKey(Map.of(
                        "tittel", Optional.ofNullable(journalpostDokument.getTittel()).orElse(""),
                        "filnavn", Optional.ofNullable(journalpostDokument.getFilnavn()).orElse(""),
                        "mediatype", mediaType.toString(),
                        "fil", fileId.toString()
                ))
                .build();
    }

    private File toFile(
            Long sourceApplicationId,
            String sourceApplicationInstanceId,
            JournalpostDokument journalpostDokument,
            MediaType type
    ) {
        return File
                .builder()
                .name(journalpostDokument.getFilnavn())
                .type(type)
                .sourceApplicationId(sourceApplicationId)
                .sourceApplicationInstanceId(sourceApplicationInstanceId)
                .encoding("UTF-8")
                .base64Contents(journalpostDokument.getDokumentBase64())
                .build();
    }

}
