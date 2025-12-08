package com.library.manager.domain;

import lombok.Getter;

@Getter
public enum BookGenre {

    FICTION("FICTION"),
    NON_FICTION("NON_FICTION"),
    CLASSIC("CLASSIC"),
    MYSTERY("MYSTERY"),
    HISTORICAL_FICTION("HISTORICAL_FICTION"),
    FANTASY("FANTASY"),
    ROMANCE("ROMANCE"),
    SCIENCE_FICTION("SCIENCE_FICTION"),
    CHILDREN("CHILDREN"),
    ESSAY("ESSAY"),
    ADVENTURE("ADVENTURE"),
    OTHER("OTHER");

    private final String displayName;

    BookGenre(String displayName) {
        this.displayName = displayName;
    }
}
