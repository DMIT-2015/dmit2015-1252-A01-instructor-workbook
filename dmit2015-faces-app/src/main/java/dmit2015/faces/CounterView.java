package dmit2015.faces;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

/**
 * View-scoped backing bean: lives across postbacks on the SAME view.
 * Destroyed when navigating away to a different view.
 */
@Named("counterView")
@ViewScoped // Survives postbacks (including AJAX) on this view; Serializable required
public class CounterView implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(CounterView.class.getName());

    @Getter
    @Setter
    private int counter = 0;

    @PostConstruct // Runs after @Inject fields are initialized (once per view instance)
    public void init() {
        // Initialize view state (avoid heavy I/O here)
        // Example: preload data for this view
        // selectedCounter = new Counter();
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

    public void onClear() {
        // Reset view state

        // selectedCounter = null;
    }

    /**
     * Log server-side and show a concise root-cause chain in the UI.
     * Assumes the page includes <p:messages id="error" />.
     */
    protected void handleException(Throwable ex, String userMessage) {
        LOG.log(Level.SEVERE, userMessage != null ? userMessage : "Unhandled error", ex);

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
            // No FacesContext available; skip UI message safely.
        }
    }
}