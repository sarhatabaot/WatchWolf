package dev.watchwolf.server.timings;

import dev.watchwolf.server.Server;
import dev.watchwolf.utils.ServerTypeGetter;

public class TimingsManagerFactory {
    public static TimingsOperator build(Server watchwolf) {
        boolean isPaper = ServerTypeGetter.isPaper();
        watchwolf.getLogger().info("Running timings manager in " + (isPaper ? "Paper" : "Spigot") + " mode");

        if (isPaper) return new PaperTimingsManager(watchwolf);
        return new SpigotTimingsManager(watchwolf);
    }
}
