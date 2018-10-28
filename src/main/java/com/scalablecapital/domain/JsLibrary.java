package com.scalablecapital.domain;

import java.util.Objects;

import static org.jsoup.helper.Validate.notNull;

/*
 * could be extended to have versions and to handle minimized scripts
 * */
public class JsLibrary {
    private final String library;

    public JsLibrary(String library) {
        notNull(library, "Not allowed nulls");
        this.library = library;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsLibrary jsLibrary = (JsLibrary) o;
        return Objects.equals(library, jsLibrary.library);
    }

    @Override
    public int hashCode() {
        return Objects.hash(library);
    }

    @Override
    public String toString() {
        return library;
    }
}
