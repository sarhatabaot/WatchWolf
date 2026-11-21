package dev.watchwolf.server;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * @author MineX <a href="https://www.spigotmc.org/threads/capturing-console-output.307132/">https://www.spigotmc.org/threads/capturing-console-output.307132/</a>
 */
public class LogAppender extends AbstractAppender implements CommandRunner {
    private static final int WAIT_FOR_MESSAGE_TIMEOUT = 4000;
    private static final int WAIT_BETWEEN_MESSAGES_TIMEOUT = 40;

    private final JavaPlugin plugin;
    private ArrayList<String> logs = null;

    public LogAppender(JavaPlugin plugin) {
        // do your calculations here before starting to capture
        super("MyLogAppender", null, null);
        this.plugin = plugin;
        start();
    }

    @Override
    public void append(LogEvent event) {
        // if you don`t make it immutable, then you may have some unexpected behaviours
        LogEvent log = event.toImmutable();

        String msg = log.getMessage().getFormattedMessage();
        synchronized (this) {
            if (this.logs == null) return;
            this.logs.add(msg); // TODO some WatchWolf msg are being appended as command return
        }
    }

    /**
     * Important: this needs to be run from a separate thread
     * Code based on the WatchWolf Client MineflayerClient's `send_command`
     * @param cmd Command to run
     * @return Response to the command
     */
    @Override
    public String runCommand(String cmd) {
        // clear the previous logs
        synchronized (this) {
            this.logs = new ArrayList<>();
        }

        // run the command
        Bukkit.dispatchCommand(this.plugin.getServer().getConsoleSender(), cmd);

        // get the command log
        long startTime = System.currentTimeMillis();
        int previousLength = 0;
        while ((System.currentTimeMillis() - startTime) < LogAppender.WAIT_FOR_MESSAGE_TIMEOUT) {
            int length;
            synchronized (this) {
                length = this.logs.size();
            }

            if (length > 0 && previousLength == length) break; // timeout between messagess
            previousLength = length;
            try {
                Thread.sleep(WAIT_BETWEEN_MESSAGES_TIMEOUT);
            } catch (InterruptedException e) {}
        }

        // it should be all done; now get the response
        String response;
        synchronized (this) {
            response = String.join("\n", this.logs);
            this.logs = null; // stop getting data
        }

        return response;
    }
}