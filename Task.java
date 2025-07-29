import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private boolean done;
    private String dueDate;  // free form string now
    private String category;

    public Task(String name, boolean done, String dueDate, String category) {
        this.name = name;
        this.done = done;
        this.dueDate = dueDate;
        this.category = category;
    }
    public String getName() { return name; }
    public boolean isDone() { return done; }
    public String getDueDate() { return dueDate; }
    public String getCategory() { return category; }
    public void toggleDone() { done = !done; }
    @Override
    public String toString() { return name; }
}