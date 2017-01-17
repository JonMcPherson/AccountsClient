package com.mojang.api.profiles.minecraft;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.mojang.api.profiles.Profile;

public class MinecraftProfile extends Profile {

    private final Textures textures;

    public MinecraftProfile(UUID id, String name, Textures textures) {
        super(id, name);
        this.textures = textures;
    }

    public Textures getTextures() {
        return textures;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), textures);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MinecraftProfile)) {
            return false;
        }
        MinecraftProfile other = (MinecraftProfile) obj;
        return super.equals(other) && textures.equals(other.textures);
    }

    public static class Textures {

        private final Skin skin;
        private final String capeUrl;

        public Textures(Skin skin, String capeUrl) {
            this.skin = skin;
            this.capeUrl = capeUrl;
        }

        public Optional<Skin> getSkin() {
            return Optional.ofNullable(skin);
        }

        public Optional<String> getCapeUrl() {
            return Optional.ofNullable(capeUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(skin, capeUrl);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Textures)) {
                return false;
            }
            Textures other = (Textures) obj;
            return Objects.equals(skin, other.skin) && Objects.equals(capeUrl, other.capeUrl);
        }

    }

    public static class Skin {

        private final String url;
        private final boolean slimModel;

        public Skin(String url, boolean slimModel) {
            this.url = url;
            this.slimModel = slimModel;
        }

        public String getUrl() {
            return url;
        }

        public boolean isSlimModel() {
            return slimModel;
        }

        @Override
        public int hashCode() {
            return Objects.hash(url, slimModel);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Skin)) {
                return false;
            }
            Skin other = (Skin) obj;
            return url.equals(other.url) && slimModel == other.slimModel;
        }

    }

}
