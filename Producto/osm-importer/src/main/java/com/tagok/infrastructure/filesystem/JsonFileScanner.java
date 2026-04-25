package com.tagok.infrastructure.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonFileScanner 
{
    private final Path basePath;

    public JsonFileScanner(String folderPatch)
    {
        this.basePath = Paths.get(folderPatch);
    }

    public List<Path> scanJsonFiles() throws IOException
    {
        try (Stream<Path> files = Files.list(basePath))
        {
            return files
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .collect(Collectors.toList());
        }
    }
}
