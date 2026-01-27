package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

@Data   // includes @Getter,@Setter,@ToString,etc
@NoArgsConstructor
public class Task {

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255,message = "Description must between {min} and {max} characters")
    private String description;

    private TaskPriority priority;

    private boolean done;

    // Copy constructor
    public Task(Task other) {
       setDescription(other.getDescription());
       setPriority(other.getPriority());
       setDone(other.isDone());
    }

    public static Task copyOf(Task other) {
        return new Task(other);
    }

    public static Task of(Faker faker) {
        Task currentTask = new Task();
        currentTask.setDescription("Nuke " + faker.fallout().location());
        currentTask.setPriority(TaskPriority.MEDIUM);
        currentTask.setDone(faker.bool().bool());
        return currentTask;
    }



}
