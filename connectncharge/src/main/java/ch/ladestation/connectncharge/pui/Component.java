package ch.ladestation.connectncharge.pui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstraction of a component
 */
public abstract class Component {
    /**
     * Logger instance
     */
    protected final Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param milliseconds Time in milliseconds to sleep
     */
    void delay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
