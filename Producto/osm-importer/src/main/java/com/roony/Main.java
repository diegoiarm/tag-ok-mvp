package com.roony;

import com.roony.domain.filter.BoundingBoxFilter;
import com.roony.infrastructure.database.DataBaseConfiguration;
import com.roony.infrastructure.database.DatabaseInitializer;
import com.roony.infrastructure.database.RoutingInitializer;
import com.roony.infrastructure.filesystem.JsonFileScanner;
import com.roony.infrastructure.parser.OsmJsonParser;
import com.roony.infrastructure.parser.ParseResult;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import javax.sql.DataSource;

public class Main 
{
    public static void main(String[] args) throws Exception 
    {
        List<Path> files = scanFiles();

        if (files.isEmpty())
            return;

        DataSource ds = DataBaseConfiguration.getDataSource();

        DatabaseInitializer.initialize(ds);

        if (files.size() == 1)
            processSingleFile(files.get(0), ds);
        else
            processMultipleFiles(files, ds);

        RoutingInitializer.initialize(ds);

        System.out.println("Procesamiento completado.");
    }

    private static List<Path> scanFiles() 
    {
        try 
        {
            JsonFileScanner scanner =
                new JsonFileScanner("datos-calles");

            List<Path> files =
                scanner.scanJsonFiles();

            if (files.isEmpty())
                System.out.println("No se encontraron archivos JSON.");

            return files;
        }
        catch (Exception e) 
        {
            throw new RuntimeException("Error escaneando archivos JSON", e);
        }
    }

    private static void processSingleFile(
        Path file,
        DataSource ds)
    {
        OsmJsonParser parser = new OsmJsonParser(BoundingBoxFilter.santiagoFiltering(), ds);

        printResult(parser.parse(file));
    }

    private static void processMultipleFiles(List<Path> files, DataSource ds) throws Exception
    {
        int threads = Runtime.getRuntime()
            .availableProcessors();

        ExecutorService executor = Executors.newFixedThreadPool(threads);

        try
        {
            List<Future<ParseResult>> futures =
                new ArrayList<>();

            for(Path file : files)
            {
                futures.add(
                    executor.submit(() ->
                        new OsmJsonParser(
                            BoundingBoxFilter.santiagoFiltering(),
                            ds
                        ).parse(file)));
            }

            for(Future<ParseResult> future : futures)
            {
                printResult(future.get());
            }
        }
        finally
        {
            executor.shutdown();
            executor.awaitTermination(
                1,
                TimeUnit.MINUTES);
        }
    }

    private static void printResult(ParseResult result)
    {
        if(result.errors() == -1)
        {
            System.out.printf("%s ERROR (archivo no procesable)%n", result.fileName());
            return;
        }

        System.out.printf(
            "%s accepted=%d rejected=%d errors=%d%n",
            result.fileName(),
            result.accepted(),
            result.rejected(),
            result.errors()
        );
    }
}