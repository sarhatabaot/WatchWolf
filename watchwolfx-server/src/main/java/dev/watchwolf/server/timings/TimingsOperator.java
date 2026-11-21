package dev.watchwolf.server.timings;

import java.util.concurrent.TimeoutException;

public interface TimingsOperator {
    void startTimings();

    /**
     * Stops the timings and get the path (url) to the report
     * @return Url to the report
     * @throws TimeoutException In Paper's Timings v2 you have to be at least 2 minutes recording
     */
    String stopTimings() throws TimeoutException;
}
