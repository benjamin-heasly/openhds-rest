package org.openhds.domain.util;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.repository.contract.AuditableExtIdRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class DeleteQuery<T extends AuditableExtIdEntity, V extends AuditableExtIdRepository<T>> {
    private AbstractAuditableExtIdService<T, V> service;
    private QueryStrategy<T> strategy;

    public DeleteQuery(AbstractAuditableExtIdService<T, V> service, QueryStrategy<T> strategy) {
        this.service = service;
        this.strategy = strategy;
    }

    public List<T> dependsOn(String id) {
        T target = service.findOne(id);
        ZonedDateTime after = target.getCollectionDateTime();
        ZonedDateTime before = ZonedDateTime.now();

        EntityIterator<T> entities = service.findByCollectionDateTime(new Sort("collectionDateTime"), after, before);

        return StreamSupport.stream(entities.spliterator(), false)
                .filter(e -> strategy.dependsOn(target, e))
                .collect(Collectors.toList());
    }
}
