package com.todo.project.exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(String type, Long id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found.", type, attribute, value));
    }

    public EntityNotFoundException(String e) {
        super(e);
    }
}
