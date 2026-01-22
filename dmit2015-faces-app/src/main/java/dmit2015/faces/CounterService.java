package dmit2015.faces;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

/**
 * Application-scoped bean: one instance shared by all users.
 * Use for app-wide config, caches, or utilities.
 * Avoid per-user UI state here.
 */
@Named("counterService")
@ApplicationScoped // Shared across everyone; not for per-user state
public class CounterService {

    private static final Logger LOG = Logger.getLogger(CounterService.class.getName());

    @Getter
    @Setter
    private int counter = 0;

    @PostConstruct // Runs after @Inject fields are initialized
    public void init() {
        // Initialize app-wide resources or caches here (optional).
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

    /**
     * Logs details server-side and shows a user-friendly error to the UI.
     * Assumes the page includes <p:messages id="error" />.
     */
    protected void handleException(Throwable ex, String userMessage) {
        // 1) Log for developers/ops
        LOG.log(Level.SEVERE, userMessage != null ? userMessage : "Unhandled error", ex);

        // 2) Build a concise cause chain (demo-friendly)
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

        // 3) Tell the user (if Faces request is active)
        try {
            Messages.create(userMessage != null ? userMessage : "An unexpected error occurred.")
                    .detail(details.toString())
                    .error()
                    .add("messages");
        } catch (Throwable ignored) {
            // FacesContext not available; UI message suppressed safely.
        }
    }
}