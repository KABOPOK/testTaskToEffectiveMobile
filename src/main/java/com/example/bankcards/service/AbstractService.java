package com.example.bankcards.service;

public abstract class AbstractService {
    private static final String NOT_FOUND = "Entity with id %s not found";

    protected static <T> T getOrThrow(){ return null; };
}
