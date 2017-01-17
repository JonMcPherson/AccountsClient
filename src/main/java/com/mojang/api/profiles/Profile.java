package com.mojang.api.profiles;

import java.util.Objects;
import java.util.UUID;

public class Profile {

    private final UUID id;
    private final String name;

    public Profile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Profile(" + id + ", " + name + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Profile)) {
            return false;
        }
        Profile other = (Profile) obj;
        return id.equals(other.id) && name.equals(other.name);
    }

}
