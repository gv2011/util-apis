package com.github.gv2011.util.beans;

import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonNode;

public interface TypeResolver<B> {

    Class<? extends B> resolve(JsonNode json);

    Opt<String> typePropertyName();

    default void addTypeProperty(final BeanBuilder<? extends B> builder){}

}
