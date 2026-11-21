package dev.watchwolf.server;

public interface CommandRunner {
    /**
     * Runs a command, capturing its output.
     * @param cmd Command to run
     * @return Response to the runned command ("" if none)
     */
    String runCommand(String cmd);
}
