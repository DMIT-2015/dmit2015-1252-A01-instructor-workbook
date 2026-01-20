package dmit2015.faces;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

/**
 * Session-scoped backing bean: per-user state that persists for the HTTP session.
 * Implements Serializable for passivation.
 */
@Named("counterSession")
@SessionScoped // Lives for the user's session; store only per-user state
public class CounterSession implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(CounterSession.class.getName());

    @Getter
    @Setter
    private int counter = 0;

    @PostConstruct // Runs once per session for this bean, after DI completes
    public void init() {
        // Initialize per-user defaults (lightweight!). Avoid heavy IO here.
        // this.counters = counterService.findAll(); // example
    }

    public void onSubmit() {
        try {
            // Increment counter by 1
            counter += 1;
            // Add an info feedback message
            Messages.addGlobalInfo("Counter = {0}", counter);
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
    }

    public void onSelected() {
        try {
            // TODO: handle row selection, etc.
        } catch (Exception ex) {
            handleException(ex, "Unable to select item.");
        }
    }

    public void onClear() {
        // Reset per-user state

        // selectedCounter = null;
        // counters = Collections.emptyList();
    }

    /**
     * Log server-side and show a concise root-cause chain in the UI.
     * Assumes <p:messages id="error" /> is present in the page.
     */
    protected void handleException(Throwable ex, String userMessage) {
        LOG.log(Level.SEVERE, userMessage, ex);

        StringBuilder details = new StringBuilder();
        Throwable t = ex;
        while (t != null) {
            String msg = t.getMessage();
            if (msg != null && !msg.isBlank()) {
                details.append(t.getClass().getSimpleName())
                        .append(": ")
                        .append(msg);
                if (t.getCause() != null) details.append("  Caused by: ");
            }
            t = t.getCause();
        }

        try {
            Messages.create(userMessage != null ? userMessage : "An unexpected error occurred.")
                    .detail(details.toString())
                    .error()
                    .add("messages");
        } catch (Throwable ignored) {
            // No FacesContext available; skip UI notification safely.
        }
    }
}
