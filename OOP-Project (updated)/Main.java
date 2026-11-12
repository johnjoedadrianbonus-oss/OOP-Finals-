import java.math.BigDecimal;
import java.util.Scanner;
import java.util.List;

public class Main {
    private final AccountManager accountManager;
    private final Scanner scanner;
    private User currentUser;
    private int nextDepositId;

    public Main() {
        this.accountManager = new AccountManager();
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        this.nextDepositId = 1;
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    public void start() {
        if (!authenticate()) {
            scanner.close();
            return;
        }

        boolean running = true;
        while (running) {
            displayMenu();
            System.out.print("Choose option (1-7): ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    addGoal();
                    break;
                case "2":
                    listGoals();
                    break;
                case "3":
                    addDeposit();
                    break;
                case "4":
                    goalProgress();
                    break;
                case "5":
                    depositHistory();
                    break;
                case "6":
                    deleteGoal();
                    break;
                case "7":
                    System.out.println("Thank you for using the app. Keep saving!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
            System.out.println();
        }
        scanner.close();
    }

    private boolean authenticate() {
        while (true) {
            System.out.println("===============================");
            System.out.println("=== Savings and Goal Tracker ===");
            System.out.println("===============================");
            System.out.println("1. Log In");
            System.out.println("2. Create Account");
            System.out.println("3. Exit");
            System.out.println("===============================");
            System.out.print("Choose option (1-3): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (login()) {
                        return true;
                    }
                    break;
                case "2":
                    createAccount();
                    break;
                case "3":
                    System.out.println("Thank you for using the app. Keep saving!");
                    return false;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
            System.out.println();
        }
    }

    private boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = accountManager.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful. Welcome, " + username + "!");
            return true;
        }

        System.out.println("Incorrect username or password.");
        return false;
    }

    private void createAccount() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();

        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        if (accountManager.usernameExists(username)) {
            System.out.println("That username is already taken. Please choose another.");
            return;
        }

        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();

        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        System.out.print("Confirm password: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return;
        }

        accountManager.createAccount(username, password);
        System.out.println("Account created successfully! You can now log in.");
    }

    private void displayMenu() {
        System.out.println("===============================");
        System.out.println("=== Savings and Goal Tracker ===");
        System.out.println("===============================");
        System.out.println("Logged in as: " + currentUser.getUsername());
        System.out.println("============ Menu: ============");
        System.out.println("===============================");
        System.out.println("1. Add Goal");
        System.out.println("2. List of Goals");
        System.out.println("3. Add Deposit");
        System.out.println("4. Goal Progress");
        System.out.println("5. Deposit History");
        System.out.println("6. Delete Goal");
        System.out.println("7. Exit");
        System.out.println("===============================");
    }

    private void addGoal() {
        System.out.println("Great! Let's set up a new savings goal.");
        System.out.print("Enter goal name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Goal name cannot be empty.");
            return;
        }

        System.out.print("Target amount: ");
        BigDecimal targetAmount;
        try {
            targetAmount = TrackerInput.parseAmount(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.print("Target deadline (YYYY-MM-DD or leave blank if none): ");
        String deadline = scanner.nextLine().trim();

        Goal newGoal;
        try {
            newGoal = new Goal(name, targetAmount, deadline);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        currentUser.addGoal(newGoal);

        System.out.println();
        printSummary(newGoal);
        System.out.printf("You are now tracking %d goal(s).%n", currentUser.getGoals().size());
    }

    private void listGoals() {
        System.out.println("===== List Of Goals =====");
        System.out.println("    All amounts in PHP.");
        System.out.println();

        List<Goal> goals = currentUser.getGoals();
        if (goals.isEmpty()) {
            System.out.println("You have no goals yet.");
            return;
        }

        for (Goal g : goals) {
            System.out.printf("#%d %-15s %s  PHP %,.2f / PHP %,.2f (%.1f%%) - %s%n",
                    g.getId(), g.getName(), g.getProgressBar(),
                    g.getSavedAmount(), g.getGoalAmount(),
                    g.getProgressPercentage(), g.getStatus());
        }
    }

    private void addDeposit() {
        if (currentUser.getGoals().isEmpty()) {
            System.out.println("You have no goals yet. Please add a goal first.");
            return;
        }

        listGoals();
        System.out.print("Enter goal ID to deposit to: ");
        int goalId;
        try {
            goalId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid goal ID.");
            return;
        }

        Goal goal = currentUser.findGoalById(goalId);
        if (goal == null) {
            System.out.println("Goal not found.");
            return;
        }

        System.out.print("Deposit amount: ");
        BigDecimal amount;
        try {
            amount = TrackerInput.parseAmount(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.nextLine().trim();

        System.out.print("Note (optional): ");
        String note = scanner.nextLine().trim();

        try {
            Deposit deposit = goal.addDeposit(nextDepositId, amount, date, note);
            nextDepositId++;
            System.out.printf("Deposited PHP %,.2f to \"%s\" on %s.%n",
                    deposit.getAmount(), deposit.getGoalName(), deposit.getDate());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteGoal() {
        if (currentUser.getGoals().isEmpty()) {
            System.out.println("You have no goals yet.");
            return;
        }

        listGoals();
        System.out.print("Enter goal ID to delete: ");
        int goalId;
        try {
            goalId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid goal ID.");
            return;
        }

        Goal goal = currentUser.findGoalById(goalId);
        if (goal == null) {
            System.out.println("Goal not found.");
            return;
        }

        System.out.printf("Are you sure you want to delete \"%s\"? This cannot be undone. (y/n): ", goal.getName());
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        currentUser.removeGoal(goalId);
        System.out.printf("Goal \"%s\" has been deleted.%n", goal.getName());
    }

    private void goalProgress() {
        List<Goal> goals = currentUser.getGoals();
        if (goals.isEmpty()) {
            System.out.println("You have no goals yet.");
            return;
        }

        for (Goal g : goals) {
            System.out.printf("#%d %s%n", g.getId(), g.getName());
            System.out.printf("  %s  %.1f%% - %s%n", g.getProgressBar(), g.getProgressPercentage(), g.getStatus());
            System.out.printf("  Saved: PHP %,.2f / PHP %,.2f%n", g.getSavedAmount(), g.getGoalAmount());
            System.out.println("  Deadline: " + (g.getDeadline() != null ? g.getDeadline() : "no deadline"));
            System.out.println();
        }
    }

    private void depositHistory() {
        List<Goal> goals = currentUser.getGoals();
        if (goals.isEmpty()) {
            System.out.println("You have no goals yet.");
            return;
        }

        listGoals();
        System.out.print("Enter goal ID to view deposit history (blank for all): ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            for (Goal g : goals) {
                printDepositHistoryForGoal(g);
            }
            return;
        }

        int goalId;
        try {
            goalId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid goal ID.");
            return;
        }

        Goal goal = currentUser.findGoalById(goalId);
        if (goal == null) {
            System.out.println("Goal not found.");
            return;
        }

        printDepositHistoryForGoal(goal);
    }

    private void printSummary(Summarizable record) {
        System.out.println(record.getSummary());
    }

    private void printDepositHistoryForGoal(Goal goal) {
        System.out.println("--- " + goal.getName() + " ---");
        List<Deposit> history = goal.getDepositHistory();
        if (history.isEmpty()) {
            System.out.println("  No deposits yet.");
            return;
        }
        for (Deposit d : history) {
            printSummary(d);
        }
    }
}