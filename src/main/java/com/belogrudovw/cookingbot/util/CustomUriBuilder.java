package com.belogrudovw.cookingbot.util;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.belogrudovw.cookingbot.util.StringUtil.encode;

public final class CustomUriBuilder {

    private String path;
    private List<String> queries;
    private static final ObjectMapper mapper = new ObjectMapper();

    private CustomUriBuilder() {
        queries = new ArrayList<>();
    }

    public static CustomUriBuilder builder() {
        return new CustomUriBuilder();
    }

    public CustomUriBuilder path(String path) {
        this.path = path;
        return this;
    }

    public CustomUriBuilder queries(List<String> queries) {
        this.queries.addAll(queries);
        return this;
    }

    public CustomUriBuilder query(String query) {
        this.queries.add(query);
        return this;
    }

    public CustomUriBuilder queryParam(String param, String value) {
        this.queries.add(param + "=" + encode(value));
        return this;
    }

    public CustomUriBuilder queryParam(String param, Object value) {
        try {
            String parsedValue = mapper.writeValueAsString(value);
            this.queries.add(param + "=" + encode(parsedValue));
            return this;
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can't parse JSON from class: " + Object.class, e);
        }
    }

    public String build() {
        return queries.stream()
                .collect(Collectors.joining("&", path + "?", ""));
    }
}