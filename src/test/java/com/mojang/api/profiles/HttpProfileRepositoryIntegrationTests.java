package com.mojang.api.profiles;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.mojang.api.profiles.minecraft.HttpProfileRepository;
import com.mojang.api.profiles.minecraft.MinecraftProfileRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@RunWith(JUnit4.class)
public class HttpProfileRepositoryIntegrationTests {

    @Test
    public void findProfilesByNames_existingNameProvided_returnsProfile() throws Exception {
        ProfileRepository repository = new HttpProfileRepository();

        Profile[] profiles = repository.findProfilesByNames("mollstam");

        assertThat(profiles.length, is(1));
        assertThat(profiles[0].getName(), is(equalTo("mollstam")));
        assertThat(profiles[0].getId(), is(equalTo(UUID.fromString("f8cdb683-9e90-43ee-a819-39f85d9c5d69"))));
    }

    @Test
    public void findProfilesByNames_existingMultipleNamesProvided_returnsProfiles() throws Exception {
        ProfileRepository repository = new HttpProfileRepository();

        Profile[] profiles = repository.findProfilesByNames("mollstam", "KrisJelbring");

        assertThat(profiles.length, is(2));
        assertThat(profiles[0].getName(), is(equalTo("KrisJelbring")));
        assertThat(profiles[0].getId(), is(equalTo(UUID.fromString("7125ba8b-1c86-4508-b92b-b5c042ccfe2b"))));
        assertThat(profiles[1].getName(), is(equalTo("mollstam")));
        assertThat(profiles[1].getId(), is(equalTo(UUID.fromString("f8cdb683-9e90-43ee-a819-39f85d9c5d69"))));
    }

    @Test
    public void findProfilesByNames_nonExistingNameProvided_returnsEmptyArray() throws Exception {
        ProfileRepository repository = new HttpProfileRepository();

        Profile[] profiles = repository.findProfilesByNames("doesnotexist$*not even legal");

        assertThat(profiles.length, is(0));
    }

    @Test
    public void findProfileById_existingNameProvided_returnsProfile() throws Exception {
        MinecraftProfileRepository repository = new HttpProfileRepository();

        UUID playerId = UUID.fromString("c35a67c9-b797-469f-a893-cf81b4104898");
        Profile profile = repository.findProfileById(playerId);

        assertThat(profile, notNullValue());
        assertThat(profile.getName(), is(equalTo("Weasel_Squeezer")));
        assertThat(profile.getId(), is(equalTo(playerId)));
    }

}
