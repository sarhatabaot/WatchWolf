package dev.watchwolf.server.timings;

import dev.watchwolf.server.ExtendedPetitionManager;
import dev.watchwolf.server.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperTimingsManager extends ExtendedPetitionManager implements TimingsOperator {
    /**
     * Paper doesn't return directly the timings report URL; we have to listen for messages till it appears
     */
    public static class PaperTimingsCommandLogger extends AbstractAppender {
        private final Server watchwolf;
        private boolean requesting;
        private String foundUrl;
        private final Object callbackResolved;

        public PaperTimingsCommandLogger(Server watchwolf) {
            super("PaperTimingsCommandLogger", null, null);
            this.watchwolf = watchwolf;
            this.callbackResolved = new Object();
            this.requesting = false;
            start();
        }

        @Override
        public void append(LogEvent event) {
            // if you don`t make it immutable, then you may have some unexpected behaviours
            LogEvent log = event.toImmutable();

            String msg = log.getMessage().getFormattedMessage();
            synchronized (callbackResolved) {
                if (!this.requesting) return; // still not reading

                Pattern p = Pattern.compile("https:\\/\\/timings\\.aikar\\.co\\/dev\\/\\?id=\\S+");
                Matcher m = p.matcher(msg);

                if (msg.contains("Please wait at least 3 minutes before generating a Timings report.")) {
                    // Paper's timings v2 (>1.9)
                    this.foundUrl = null; // timeout
                    this.callbackResolved.notifyAll();
                }
                else if (m.find()) {
                    this.foundUrl = m.group();
                    this.callbackResolved.notifyAll();
                }
            }
        }

        public String requestTimings() throws TimeoutException {
            synchronized (callbackResolved) {
                try {
                    while (this.requesting) Thread.sleep(500); // there's another waiting
                } catch (InterruptedException ignore) {}
                this.requesting = true; // inform to start recording
            }

            Bukkit.getScheduler().runTask(this.watchwolf, ()->Bukkit.dispatchCommand(this.watchwolf.getServer().getConsoleSender(), "timings report"));

            String url = null;
            synchronized (callbackResolved) {
                try {
                    this.callbackResolved.wait();

                    url = this.foundUrl;

                    this.requesting = false; // done
                } catch (InterruptedException ignore) {}
            }

            if (url == null) throw new TimeoutException("Timings don't meet the criteria");
            return url;
        }
    }

    private PaperTimingsCommandLogger logger;

    public PaperTimingsManager(Server watchwolf, Plugin _watchwolf) {
        super(watchwolf, _watchwolf);

        final org.apache.logging.log4j.core.Logger logger = (org.apache.logging.log4j.core.Logger) LogManager.getRootLogger();
        this.logger = new PaperTimingsCommandLogger(watchwolf);
        logger.addAppender(this.logger);
    }

    public PaperTimingsManager(Server watchwolf) {
        this(watchwolf, watchwolf);
    }

    @Override
    public void startTimings() {
        try {
            this.getWatchWolf().runCommand("timings on");
        } catch (IOException ignore) {}
    }

    /**
     *              /!\ WARNING /!\
     * NOT MC-SAFE; DO NOT RUN ON THE SAME THREAD AS PAPER
     */
    @Override
    public String stopTimings() throws TimeoutException {
        String result = this.logger.requestTimings();
        Bukkit.getScheduler().runTask(this.getWatchWolf(), ()->Bukkit.dispatchCommand(this.getWatchWolf().getServer().getConsoleSender(), "timings off"));
        return result;
    }
}
