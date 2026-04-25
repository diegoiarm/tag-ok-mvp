package com.tagok;

import com.tagok.domain.filter.BoundingBoxFilter;
import com.tagok.infrastructure.DataBaseConfiguration;
import com.tagok.infrastructure.filesystem.JsonFileScanner;
import com.tagok.infrastructure.parser.OsmJsonParser;
import com.tagok.infrastructure.parser.ParseResult;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import javax.sql.DataSource;

public class Main 
{
    public static void main(String[] args) throws Exception 
    {
        DataSource ds = DataBaseConfiguration.getDataSource();

        JsonFileScanner scanner = new JsonFileScanner("src/main/resources/datos-calles");
        List<Path> files = scanner.scanJsonFiles();

        OsmJsonParser parser = new OsmJsonParser(BoundingBoxFilter.santiagoFiltering(), ds);

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        List<Future<ParseResult>> futures = new ArrayList<>();
        for (Path file : files) 
        {
            Future<ParseResult> future = executor.submit(() -> parser.parse(file));
            futures.add(future);
        }

        for (int i = 0; i < files.size(); i++) 
        {
            try 
            {
                ParseResult result = futures.get(i).get();
                if (result.errors() == -1) 
                {
                    System.out.printf("%s ERROR (archivo no procesable)%n", result.fileName());
                } 
                else
                {
                    System.out.printf("%s accepted=%d rejected=%d errors=%d%n",
                            result.fileName(), result.accepted(), result.rejected(), result.errors());              
                }
            } 
            catch (ExecutionException e) 
            {
                System.err.println("Error procesando " + files.get(i).getFileName() + ": " + e.getCause().getMessage());
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
                System.err.println("Proceso interrumpido");
                break;
            }
        }

        executor.shutdown();
        // Esperar a que terminen todas las tareas (ya deberían haber terminado)
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("Procesamiento completado.");
    }
}