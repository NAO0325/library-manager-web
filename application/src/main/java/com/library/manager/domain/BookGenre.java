package com.library.manager.domain;

import lombok.Getter;

@Getter
public enum BookGenre {

    FICTION("Ficción"),
    NON_FICTION("No ficción"),
    CLASSIC("Clásico"),
    MYSTERY("Misterio"),
    HISTORICAL_FICTION("Ficción histórica"),
    FANTASY("Fantasía"),
    ROMANCE("Romance"),
    SCIENCE_FICTION("Ciencia ficción"),
    CHILDREN("Infantil"),
    ESSAY("Ensayo"),
    ADVENTURE("Aventuras"),
    OTHER("Otro");

    private final String displayName;

    BookGenre(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
