import java.util.List;
import java.util.ArrayList;

public class User {
    private final String username;
    private final String password;
    private final List<Goal> goals;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.goals = new ArrayList<>();
        seedInitialGoals();
    }

    private void seedInitialGoals() {
        goals.add(new Goal("Phone", 20000.00, "2026-11-30"));
        goals.add(new Goal("Laptop", 100000.00, "2026-12-25"));
        goals.add(new Goal("Computer", 300000.00, "2027-02-14"));
        goals.add(new Goal("PS5", 40000.00, "2027-04-09"));
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public Goal findGoalById(int id) {
        for (Goal g : goals) {
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    public boolean removeGoal(int id) {
        return goals.removeIf(g -> g.getId() == id);
    }
}