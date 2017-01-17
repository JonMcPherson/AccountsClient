package com.mojang.api.profiles;

import java.io.IOException;

public interface ProfileRepository {

    public Profile[] findProfilesByNames(String... names) throws IOException;

}
