package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Request-scoped backing bean: new instance per HTTP request.
 * Use for simple actions/data that don't need to persist after the response.
 */
@Named("example2InputTextAndCommandButtonRequest")
@RequestScoped // New instance per HTTP request; no Serializable required
public class Example2InputTextAndCommandButtonRequest {

    private static final Logger LOG = Logger.getLogger(Example2InputTextAndCommandButtonRequest.class.getName());

    @Getter @Setter
    @NotBlank(message = "User input is required")
    @Size(min=3,max=150, message = "User input must contain between {min} and {max} characters")
    private String userInput;

    public void onSubmit() {
        try {
            // Create a DataFaker object for generating fake data
            var faker = new Faker();
            // Add a Global Info message with a random Fallout faction
            Messages.addGlobalInfo("Welcome {0} to Fallout season 2. You have assigned to {1} faction",
                    userInput,
                    faker.fallout().faction()
            );
        } catch (Exception ex) {
            handleException(ex, "Unable to process your request.");
        }
    }

    public void onClear() {
        userInput = null;
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
            // No FacesContext available; skip UI notification safely.
        }
    }
}
