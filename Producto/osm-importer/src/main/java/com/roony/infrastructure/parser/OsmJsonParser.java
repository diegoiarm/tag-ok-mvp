package com.roony.infrastructure.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roony.domain.filter.BoundingBoxFilter;
import com.roony.domain.model.Element;
import com.roony.infrastructure.middleware.FilterResult;
import com.roony.infrastructure.middleware.Pipeline;
import com.roony.infrastructure.middleware.PipelineRunner;
import com.roony.infrastructure.middleware.SqlExportMiddleware;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class OsmJsonParser 
{
    private static final Logger logger = Logger.getLogger(OsmJsonParser.class.getName());
    private final PipelineRunner pipeline;
    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false)
        .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,false)
        .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES,false);
    private static final ElementMapper ELEMENT_MAPPER = new ElementMapper();

    public OsmJsonParser(BoundingBoxFilter boundingBoxFilter, DataSource dataSource) 
    {
        this.pipeline = new Pipeline()
            //.addBoundsFilter(boundingBoxFilter)
            .add(new SqlExportMiddleware(dataSource))
            .build();
    }

    public ParseResult parse(Path file) 
    {
        try (InputStream is = Files.newInputStream(file);
            JsonParser jp = JSON_FACTORY.createParser(is)) 
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

                while (jp.nextToken() != JsonToken.END_ARRAY)
                {
                    index++;

                    try
                    {
                        Element element = ELEMENT_MAPPER.map(jp);

                        FilterResult result = pipeline.run(
                                element,
                                index,
                                fileName);

                        switch (result)
                        {
                            case ACCEPTED -> accepted++;
                            case REJECTED -> rejected++;
                            case ERROR -> errors++;
                        }
                    }
                    catch (Exception e)
                    {
                        errors++;

                        logger.warning(
                            "Error en elemento "
                            + index
                            + " de "
                            + fileName
                            + ": "
                            + e.getMessage());
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