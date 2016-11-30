package org.openhds.domain.util;

import org.openhds.domain.contract.AuditableExtIdEntity;
import org.openhds.repository.contract.AuditableExtIdRepository;
import org.openhds.repository.results.EntityIterator;
import org.openhds.service.contract.AbstractAuditableExtIdService;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;


public class DeleteQuery<T extends AuditableExtIdEntity, V extends AuditableExtIdRepository<T>> {
    private AbstractAuditableExtIdService<T, V> service;
    private QueryStrategy<T> strategy;

    public DeleteQuery(AbstractAuditableExtIdService<T, V> service, QueryStrategy<T> strategy) {
        this.service = service;
        this.strategy = strategy;
    }

    public boolean isDeletable(String id) {
        T target = service.findOne(id);
        ZonedDateTime after = target.getCollectionDateTime();
        ZonedDateTime before = ZonedDateTime.now();

        EntityIterator<T> entities = service.findByCollectionDateTime(new Sort("collectionDateTime"), after, before);

        for (T entity: entities) {
            if(strategy.dependsOn(target, entity)) {
                return false;
            }
        }
        return true;
    }
}
