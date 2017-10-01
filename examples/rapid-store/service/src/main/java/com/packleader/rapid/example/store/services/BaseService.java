package com.packleader.rapid.example.store.services;

import com.packleader.rapid.example.store.domain.UniqueEntity;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface BaseService<T extends UniqueEntity> {
    T find(@NonNull UUID id);

    List<T> findAll();

    T create(@NonNull T entity);

    T update(@NonNull T entity);

    void delete(@NonNull UUID id);
}
