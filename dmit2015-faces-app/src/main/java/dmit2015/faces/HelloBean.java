package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named
@RequestScoped
public class HelloBean {

    @Getter @Setter
    private String userInput = "";

    public String getMessage() {
        return String.format("Hello %s", userInput);
    }


}
