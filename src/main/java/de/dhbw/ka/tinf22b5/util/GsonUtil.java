package de.dhbw.ka.tinf22b5.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.dhbw.ka.tinf22b5.chat.ChatRelatedJson;
import de.dhbw.ka.tinf22b5.net.p2p.serialization.PolymorphDeserializer;

public class GsonUtil {
    private GsonUtil() {
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(ChatRelatedJson.class, new PolymorphDeserializer<ChatRelatedJson>())
                .create();
    }
}