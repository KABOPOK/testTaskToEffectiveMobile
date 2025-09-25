package com.example.bankcards.service;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static java.lang.String.format;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DefaultService {

  protected static final String ENTITY_NOT_FOUND = "Entity with id %s not found";
  protected static final String ENTITY_ALREADY_EXIST = "Entity with id %s already exists";

  protected static  <T> T getOrThrow(UUID id, Function<UUID, Optional<T>> function) {
    return function.apply(id).orElseThrow(() -> new EntityNotFoundException(format(ENTITY_NOT_FOUND, id)));
  }


}