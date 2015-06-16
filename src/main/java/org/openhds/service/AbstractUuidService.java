package org.openhds.service;

import org.openhds.domain.contract.UuidIdentifiable;
import org.openhds.repository.UuidIdentifiableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Created by wolfe on 6/11/15.
 */
public abstract class AbstractUuidService <T extends UuidIdentifiable>{

    private final UuidIdentifiableRepository<T> repository;

    public AbstractUuidService(UuidIdentifiableRepository repository) {
        this.repository = repository;
    }

    public T findOne(String uuid){
        return repository.findOne(uuid);
    }

    public List<T> findAll(){
        return repository.findAll();
    }

    public Page findPaged(Pageable pageable){
        return repository.findAll(pageable);
    }

    public T createOrUpdate(T entity){
        return repository.save(entity);
    }


}
