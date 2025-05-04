package de.dhbw.ka.tinf22b5.net.p2p.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Arrays;

public class PolymorphDeserializer<T> implements JsonDeserializer<T> {
    public T deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        try {
            Class<?> typeClass = Class.forName(type.getTypeName());
            JsonType jsonType = typeClass.getDeclaredAnnotation(JsonType.class);

            if (jsonType == null) {
                throw new JsonParseException("Failed to deserialize json due to a missing annotation");
            }

            String property = json.getAsJsonObject().getAsJsonObject(jsonType.property()).getAsString();
            JsonSubtype[] subtypes = jsonType.subtypes();
            Class<?> subtype = Arrays.stream(subtypes)
                    .filter(subtype1 -> subtype1.name().equals(property)).findFirst()
                    .orElseThrow(IllegalArgumentException::new).clazz();

            return context.deserialize(json, subtype);
        } catch (Exception e) {
            throw new JsonParseException("Failed to deserialize json", e);
        }
    }
}