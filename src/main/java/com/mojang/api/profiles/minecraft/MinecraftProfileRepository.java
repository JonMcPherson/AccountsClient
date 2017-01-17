package com.mojang.api.profiles.minecraft;

import java.io.IOException;
import java.util.UUID;

import com.mojang.api.profiles.ProfileRepository;

public interface MinecraftProfileRepository extends ProfileRepository {

    public MinecraftProfile findProfileById(UUID uuid) throws IOException;

}
