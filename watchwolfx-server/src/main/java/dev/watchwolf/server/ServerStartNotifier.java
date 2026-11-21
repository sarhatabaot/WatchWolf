package dev.watchwolf.server;

import java.io.IOException;

public interface ServerStartNotifier {
    void onServerStart() throws IOException;
}
