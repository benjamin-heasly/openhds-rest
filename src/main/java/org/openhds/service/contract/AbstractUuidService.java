package org.openhds.service.contract;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.repository.contract.UuidIdentifiableRepository;
import org.openhds.repository.queries.QueryRange;
import org.openhds.repository.queries.QueryValue;
import org.openhds.repository.queries.Specifications;
import org.openhds.repository.results.EntityIterator;
import org.openhds.repository.results.PageIterator;
import org.openhds.repository.results.PagingEntityIterator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractUuidService<T extends UuidIdentifiable, V extends UuidIdentifiableRepository<T>> {

    public final static String UNKNOWN_ENTITY_UUID = "UNKNOWN";

    protected final V repository;

    public AbstractUuidService(V repository) {
        this.repository = repository;
    }

    protected abstract T makeUnknownEntity();

    private void persistUnknownEntity() {
        T unknownEntity = makeUnknownEntity();
        unknownEntity.setUuid(UNKNOWN_ENTITY_UUID);
        repository.save(unknownEntity);
    }

    public T getUnknownEntity() {
        if (!repository.exists(UNKNOWN_ENTITY_UUID)) {
            persistUnknownEntity();
        }
        return repository.findOne(UNKNOWN_ENTITY_UUID);
    }

    public long countAll() {
        return repository.count();
    }

    public EntityIterator<T> findAll(Sort sort) {
        return iteratorFromPageable(repository::findAll, sort);
    }

    public void delete(T entity, String reason){
        repository.delete(entity);
    }

    public T findOne(String id){
        return repository.findOne(id);
    }

    public T createOrUpdate(T entity){
        return repository.save(entity);
    }

    public EntityIterator<T> findByMultipleValues(Sort sort, QueryValue... queryValues) {
        Specification<T> specification = Specifications.multiValue(queryValues);
        return iteratorFromPageable(pageable -> repository.findAll(specification, pageable), sort);
    }

    public Page<T> findByMultipleValues(Pageable pageable, QueryValue... queryValues) {
        Specification<T> specification = Specifications.multiValue(queryValues);
        return repository.findAll(specification, pageable);
    }

    public EntityIterator<T> findByMultipleValuesRanged(Sort sort, QueryRange queryRange, QueryValue... queryValues) {
        Specification<T> specification = Specifications.rangedMultiValue(queryRange, queryValues);
        return iteratorFromPageable(pageable -> repository.findAll(specification, pageable), sort);
    }

    public Page<T> findByMultipleValuesRanged(Pageable pageable, QueryRange queryRange, QueryValue... queryValues) {
        Specification<T> specification = Specifications.rangedMultiValue(queryRange, queryValues);
        return repository.findAll(specification, pageable);
    }

    // Iterate entities based on paged queries.
    protected EntityIterator<T> iteratorFromPageable(PageIterator.PagedQueryable<T> pagedQueryable, Sort sort) {
        return new PagingEntityIterator<>(new PageIterator<>(pagedQueryable, sort));
    }

}
