package com.tagok.infrastructure.parser;

import java.util.Collections;
import java.util.List;

import com.tagok.domain.model.Element;

public record ParseResult(
    String fileName,
    int accepted,
    int rejected,
    int errors,
    List<Element> firstFiveElements
) {
    public ParseResult(String fileName, int accepted, int rejected, int errors) 
    {
        this(fileName, accepted, rejected, errors, Collections.emptyList());
    }
}
