package com.mojang.api.profiles.minecraft;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.mojang.api.http.BasicHttpClient;
import com.mojang.api.http.HttpBody;
import com.mojang.api.http.HttpClient;
import com.mojang.api.http.HttpHeader;
import com.mojang.api.profiles.Profile;
import com.mojang.api.profiles.minecraft.MinecraftProfile;
import com.mojang.api.profiles.minecraft.MinecraftProfileRepository;
import com.mojang.api.profiles.minecraft.MinecraftProfile.Skin;
import com.mojang.api.profiles.minecraft.MinecraftProfile.Textures;

public class HttpProfileRepository implements MinecraftProfileRepository {

    // You're not allowed to request more than 100 profiles per go.
    private static final int IDS_PER_REQUEST = 100;
    private static final String PROFILES_BY_NAMES_API_URL = "https://api.mojang.com/profiles/minecraft";
    private static final String PROFILE_BY_ID_API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    private final Gson gson = new GsonBuilder().registerTypeAdapter(Profile.class, new ProfileDeserializer())
            .registerTypeAdapter(MinecraftProfile.class, new MinecraftProfileDeserializer()).create();
    private final HttpClient client;

    public HttpProfileRepository() {
        this(BasicHttpClient.getInstance());
    }

    public HttpProfileRepository(HttpClient client) {
        this.client = client;
    }


    @Override
    public Profile[] findProfilesByNames(String... names) throws IOException {
        List<Profile> profiles = new ArrayList<>();

        List<HttpHeader> headers = new ArrayList<HttpHeader>();
        headers.add(new HttpHeader("Content-Type", "application/json"));

        int namesCount = names.length;
        int start = 0;
        int i = 0;
        do {
            int end = IDS_PER_REQUEST * (i + 1);
            if (end > namesCount) {
                end = namesCount;
            }
            String[] namesBatch = Arrays.copyOfRange(names, start, end);
            HttpBody body = new HttpBody(gson.toJson(namesBatch));

            String response = client.post(new URL(PROFILES_BY_NAMES_API_URL), body, headers);
            Collections.addAll(profiles, gson.fromJson(response, Profile[].class));

            start = end;
            i++;
        } while (start < namesCount);

        return profiles.toArray(new Profile[profiles.size()]);
    }

    @Override
    public MinecraftProfile findProfileById(UUID uuid) throws IOException {
        List<HttpHeader> headers = new ArrayList<HttpHeader>();
        headers.add(new HttpHeader("Content-Type", "application/json"));

        URL profileByIdUrl = new URL(String.format(PROFILE_BY_ID_API_URL, toShortenedUuid(uuid)));
        String response = client.get(profileByIdUrl, headers);

        return gson.fromJson(response, MinecraftProfile.class);
    }


    private static String toShortenedUuid(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    public static UUID parseShortenedUuid(String shortenedUuid) {
        return UUID.fromString(shortenedUuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    private class ProfileDeserializer implements JsonDeserializer<Profile> {

        @Override
        public Profile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject profileObject = json.getAsJsonObject();

            UUID id = parseShortenedUuid(profileObject.get("id").getAsString());
            String name = profileObject.get("name").getAsString();

            return new Profile(id, name);
        }

    }

    private class MinecraftProfileDeserializer implements JsonDeserializer<MinecraftProfile> {

        private final Type texturesType = new TypeToken<Map<String, Object>>(){}.getType();

        @Override
        public MinecraftProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject profileObject = json.getAsJsonObject();
            UUID id = parseShortenedUuid(profileObject.get("id").getAsString());
            String name = profileObject.get("name").getAsString();

            JsonArray propertiesArray = profileObject.getAsJsonArray("properties");
            String texturesJson = new String(Base64.getDecoder().decode(getEncodedTextures(propertiesArray)), StandardCharsets.UTF_8);
            Map<String, Object> texturesObject = gson.fromJson(texturesJson, texturesType);
            Map<?, ?> texturesArray = (Map<?, ?>) texturesObject.get("textures");

            Skin skin = null;
            Map<?, ?> skinObject = (texturesArray != null ? (Map<?, ?>) texturesArray.get("SKIN") : null);
            if (skinObject != null) {
                String skinUrl = (String) skinObject.get("url");
                Map<?, ?> metadataObject = (Map<?, ?>) skinObject.get("metadata");
                boolean slimModel = metadataObject != null && "slim".equals(metadataObject.get("model"));
                skin = new Skin(skinUrl, slimModel);
            }
            String capeUrl = null;
            Map<?, ?> capeObject = (texturesArray != null ? (Map<?, ?>) texturesArray.get("CAPE") : null);
            if (capeObject != null) {
                capeUrl = (String) capeObject.get("url");
            }
            Textures textures = new Textures(skin, capeUrl);

            return new MinecraftProfile(id, name, textures);
        }

        private String getEncodedTextures(JsonArray properties) {
            for (JsonElement propertyElem : properties) {
                JsonObject property = propertyElem.getAsJsonObject();
                if (property.get("name").getAsString().equals("textures")) {
                    return property.get("value").getAsString();
                }
            }
            throw new JsonParseException("response unexpectedly missing textures property");
        }

    }

}
