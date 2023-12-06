package no.fintlabs.sak;

import no.fintlabs.gateway.instance.InstanceMapper;
import no.fintlabs.gateway.instance.model.instance.InstanceObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class SakMappingService implements InstanceMapper<Sak> {

    @Value("${fint.flyt.sourceapplication.checkSaksansvarligEpost:true}")
    boolean checkSaksansvarligEpost;

    @Value("${fint.flyt.sourceapplication.checkEmailDomain:true}")
    boolean checkEmailDomain;

    @Value("${fint.org-id}")
    private String orgId;

    public SakMappingService(
    ) {
    }

    @Override
    public Mono<InstanceObject> map(Long sourceApplicationId, Sak sak) {
        Map<String, String> valuePerKey = getStringStringMap(sak);
        return Mono.just(
                InstanceObject.builder()
                        .valuePerKey(valuePerKey)
                        .objectCollectionPerKey(Map.of(
                                "sak_samling", sak.getSakObjekter()
                                        .stream()
                                        .map(this::toInstanceObject)
                                        .toList()
                        ))
                        .build()
        );
    }

    private static Map<String, String> getStringStringMap(Sak sak) {
        Map<String, String> valuePerKey = new HashMap<>();
        valuePerKey.put("sys_id", sak.getId());
        valuePerKey.put("felt_1", sak.getFelt1());
        valuePerKey.put("felt_2", sak.getFelt2());
        valuePerKey.put("felt_3", sak.getFelt3());
        return valuePerKey;
    }

    private InstanceObject toInstanceObject(SakObject sakObject) {
        return InstanceObject
                .builder()
                .valuePerKey(Map.of(
                        "samlingsfelt_1", sakObject.getFelt1(),
                        "samlingsfelt_2", sakObject.getFelt2(),
                        "samlingsfelt_3", sakObject.getFelt3()
                ))
                .build();
    }

}
