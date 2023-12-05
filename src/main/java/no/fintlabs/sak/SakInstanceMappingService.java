package no.fintlabs.sak;

import no.fintlabs.gateway.instance.InstanceMapper;
import no.fintlabs.gateway.instance.model.instance.InstanceObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SakInstanceMappingService implements InstanceMapper<SakInstance> {

    @Value("${fint.flyt.sourceapplication.checkSaksansvarligEpost:true}")
    boolean checkSaksansvarligEpost;

    @Value("${fint.flyt.sourceapplication.checkEmailDomain:true}")
    boolean checkEmailDomain;

    @Value("${fint.org-id}")
    private String orgId;

    public SakInstanceMappingService(
    ) {
    }

    @Override
    public Mono<InstanceObject> map(Long sourceApplicationId, SakInstance sakInstance) {
        Map<String, String> valuePerKey = getStringStringMap(sakInstance);
        return Mono.just(
                InstanceObject.builder()
                        .valuePerKey(valuePerKey)
                        .objectCollectionPerKey(Map.of(
                                "sak_samling", sakInstance.getSamling()
                                        .stream()
                                        .map(this::toInstanceObject)
                                        .toList()
                        ))
                        .build()
        );
    }

    private static Map<String, String> getStringStringMap(SakInstance sakInstance) {
        Map<String, String> valuePerKey = new HashMap<>();
        valuePerKey.put("sys_id", sakInstance.getId());
        valuePerKey.put("felt_1", sakInstance.getFelt1());
        valuePerKey.put("felt_2", sakInstance.getFelt2());
        valuePerKey.put("felt_3", sakInstance.getFelt3());
        return valuePerKey;
    }

    private InstanceObject toInstanceObject(SakSamling sakSamling) {
        return InstanceObject
                .builder()
                .valuePerKey(Map.of(
                        "samlingsfelt_1", sakSamling.getFelt1(),
                        "samlingsfelt_2", sakSamling.getFelt2(),
                        "samlingsfelt_3", sakSamling.getFelt3()
                ))
                .build();
    }

}
