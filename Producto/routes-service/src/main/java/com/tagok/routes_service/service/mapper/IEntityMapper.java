package com.tagok.routes_service.service.mapper;

public interface IEntityMapper<Response, Request, Entity>
{
    Entity fromRequest(Request request);
    Response toResponse(Entity entity);
}
