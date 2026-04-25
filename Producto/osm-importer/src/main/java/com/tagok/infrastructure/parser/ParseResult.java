package com.tagok.infrastructure.parser;

public record ParseResult(
    String fileName,
    int accepted,
    int rejected,
    int errors) 
{
}
