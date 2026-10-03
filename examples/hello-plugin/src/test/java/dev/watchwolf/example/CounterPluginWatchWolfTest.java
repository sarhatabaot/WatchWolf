package dev.watchwolf.example;

import dev.watchwolf.tester.AbstractTest;
import dev.watchwolf.tester.TesterConnector;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(CounterPluginWatchWolfTest.class)
public class CounterPluginWatchWolfTest extends AbstractTest {
    @Override
    public String getConfigFile() {
        return "src/test/resources/watchwolf.yaml";
    }

    @ParameterizedTest
    @ArgumentsSource(CounterPluginWatchWolfTest.class)
    @Timeout(value = 1, unit = TimeUnit.MINUTES)
    void counterCommandKeepsStateOnTheServer(TesterConnector connector) throws IOException {
        assertContains(connector.server.runCommand("samplecounter get"), "sample-counter=0");
        assertContains(connector.server.runCommand("samplecounter add 2"), "sample-counter=2");
        assertContains(connector.server.runCommand("samplecounter get"), "sample-counter=2");
    }

    private static void assertContains(String output, String expected) {
        assertTrue(output.contains(expected), () -> "Expected '" + expected + "' in: " + output);
    }
}
