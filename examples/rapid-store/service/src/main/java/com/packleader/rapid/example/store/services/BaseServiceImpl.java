package com.packleader.rapid.example.store.services;

import com.packleader.rapid.example.store.domain.UniqueEntity;
import com.packleader.rapid.example.store.exceptions.ResourceNotFoundException;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BaseServiceImpl<T extends UniqueEntity> implements BaseService<T> {

    protected Map<UUID, T> repository = new ConcurrentHashMap<>();

    @Override
    public T find(@NonNull UUID id) {
        throwExceptionIfNotFound(id);
        return repository.get(id);
    }

    @Override
    public List<T> findAll() {
        Collection<T> allEntities = repository.values();
        return new ArrayList(allEntities);
    }

    @Override
    public T create(@NonNull T entity) {
        final UUID id = UUID.randomUUID();
        entity.setId(id);
        repository.put(id, entity);

        return entity;
    }

    @Override
    public T update(@NonNull T entity) {
        final UUID id = entity.getId();
        throwExceptionIfNotFound(id);
        repository.put(id, entity);

        return entity;
    }

    @Override
    public void delete(@NonNull UUID id) {
        throwExceptionIfNotFound(id);
        repository.remove(id);
    }

    private void throwExceptionIfNotFound(UUID uuid) {
        if (!repository.containsKey(uuid)) {
            throw new ResourceNotFoundException("No resource exists with id " + uuid);
        }
    }
}
