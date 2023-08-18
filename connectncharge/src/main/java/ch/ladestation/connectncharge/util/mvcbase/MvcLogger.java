package ch.ladestation.connectncharge.util.mvcbase;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public final class MvcLogger {
    private final Logger logger = LoggerFactory.getLogger(MvcLogger.class);

    public void logInfo(String msg) {
        logger.info(msg);
    }

    public void logError(String msg) {
        logger.error(msg);
    }

    public void logConfig(String msg) {
        logger.trace(msg);
    }

    public void logDebug(String msg) {
        logger.debug(msg);
    }
}
