package com.tagok.infrastructure.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tagok.domain.filter.BoundingBoxFilter;
import com.tagok.infrastructure.middleware.FilterResult;
import com.tagok.infrastructure.middleware.MapToDomainMiddleware;
import com.tagok.infrastructure.middleware.Pipeline;
import com.tagok.infrastructure.middleware.PipelineRunner;
import com.tagok.infrastructure.middleware.SqlExportMiddleware;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

public class OsmJsonParser 
{
    private static final Logger logger = Logger.getLogger(OsmJsonParser.class.getName());
    private final ObjectMapper mapper;
    private final PipelineRunner pipeline;

    public OsmJsonParser(BoundingBoxFilter boundingBoxFilter, DataSource dataSource) 
    {
        this.mapper = new ObjectMapper();
        this.pipeline = new Pipeline()
            //.addBoundsFilter(boundingBoxFilter)
            .add(new MapToDomainMiddleware())
            .add(new SqlExportMiddleware(dataSource))
            .build();
    }

    public ParseResult parse(Path file) 
    {
        try (InputStream is = Files.newInputStream(file);
            JsonParser jp = new JsonFactory().createParser(is)) 
            {

                if (jp.nextToken() != JsonToken.START_OBJECT) 
                {
                    logger.warning(() -> "El archivo no comienza con un objeto JSON: " + file);
                    return new ParseResult(file.getFileName().toString(), 0, 0, -1);
                }

                boolean found = false;
                while (jp.nextToken() != JsonToken.END_OBJECT) 
                {
                    if (jp.currentToken() == JsonToken.FIELD_NAME && "elements".equals(jp.getCurrentName())) 
                    {
                        jp.nextToken();
                        found = true;
                        break;
                    } 
                    else 
                    {
                        jp.skipChildren();
                    }
                }

                if (!found) 
                {
                    logger.warning(() -> "No se encontró el campo 'elements' en " + file);
                    return new ParseResult(file.getFileName().toString(), 0, 0, -1);
                }

                if (jp.currentToken() != JsonToken.START_ARRAY) 
                {
                    if (jp.currentToken() == JsonToken.VALUE_NULL) 
                    {
                        logger.warning(() -> "'elements' es null en " + file);
                    } 
                    else 
                    {
                        logger.warning(() -> "'elements' no es un array (token: " + jp.currentToken() + ") en " + file);
                    }
                    return new ParseResult(file.getFileName().toString(), 0, 0, -1);
                }

                int accepted = 0, rejected = 0, errors = 0;
                String fileName = file.getFileName().toString();
                int index = 0;

                Map<String, Object> context = new HashMap<>();

                while (jp.nextToken() != JsonToken.END_ARRAY) 
                {
                    index++;
                    JsonNode node = mapper.readTree(jp);
                    FilterResult result = pipeline.run(node, index, fileName, context);

                    switch (result) 
                    {
                        case ACCEPTED -> accepted++;
                        case REJECTED -> rejected++;
                        case ERROR -> errors++;
                    }
                }

            return new ParseResult(fileName, accepted, rejected, errors);

        } 
        catch (IOException e) 
        {
            logger.log(Level.SEVERE, "No se pudo leer o procesar " + file, e);
            return new ParseResult(file.getFileName().toString(), 0, 0, -1);
        }
    }
}