package com.roony.infrastructure.filesystem;

import java.net.URI;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonFileScanner
{
    private final Path basePath;

    public JsonFileScanner(String folder)
    {
        try
        {
            URI uri = getClass()
                .getClassLoader()
                .getResource(folder)
                .toURI();

            this.basePath = Paths.get(uri);
        }
        catch(Exception e)
        {
            throw new RuntimeException(
                "No se encontró carpeta: " + folder,
                e
            );
        }
    }

    public List<Path> scanJsonFiles() throws Exception
    {
        try(Stream<Path> files = Files.list(basePath))
        {
            return files
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .collect(Collectors.toList());
        }
    }
}