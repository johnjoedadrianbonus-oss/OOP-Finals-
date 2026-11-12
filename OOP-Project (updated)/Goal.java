import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Goal extends TrackerRecord {
    private static int idCounter = 1;

    private final String name;
    private final BigDecimal goalAmount;
    private BigDecimal savedAmount;
    private final String deadline; // Format: YYYY-MM-DD, null if none
    private final List<Deposit> depositHistory;

    public Goal(String name, double goalAmount, String deadline) {
        this(name, BigDecimal.valueOf(goalAmount), deadline);
    }

    public Goal(String name, BigDecimal goalAmount, String deadline) {
        super(idCounter++);
        this.name = name;
        this.goalAmount = TrackerInput.positiveAmount(goalAmount);
        this.savedAmount = BigDecimal.ZERO.setScale(2);
        this.deadline = (deadline == null || deadline.trim().isEmpty()) ? null : TrackerInput.date(deadline);
        this.depositHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public BigDecimal getGoalAmount() {
        return goalAmount;
    }
    public BigDecimal getSavedAmount() {
        return savedAmount;
    }
    public String getDeadline() {
        return deadline;
    }
    public List<Deposit> getDepositHistory() {
        return Collections.unmodifiableList(depositHistory);
    }

    public Deposit addDeposit(int depositID, double amount, String date, String note) {
        return addDeposit(depositID, BigDecimal.valueOf(amount), date, note);
    }

    public Deposit addDeposit(int depositID, BigDecimal amount, String date, String note) {
        amount = TrackerInput.positiveAmount(amount);
        BigDecimal remaining = goalAmount.subtract(savedAmount);
        if (amount.compareTo(remaining) > 0) {
            throw new IllegalArgumentException(String.format(
                    "Deposit exceeds target amount. You can deposit at most PHP %,.2f more toward \"%s\".",
                    remaining, this.name));
        }
        Deposit deposit = new Deposit(depositID, getId(), this.name, amount, date, note);
        this.depositHistory.add(deposit);
        savedAmount = savedAmount.add(amount);
        return deposit;
    }

    public double getProgressPercentage() {
        return savedAmount.movePointRight(2).divide(goalAmount, MathContext.DECIMAL64).doubleValue();
    }

    public String getStatus() {
        return savedAmount.compareTo(goalAmount) >= 0 ? "DONE" : "IN PROGRESS";
    }

    public String getProgressBar() {
        int totalBars = 20;
        double progressPercentage = getProgressPercentage();
        int filledBars = (int) (progressPercentage / 100 * totalBars);
        StringBuilder progressBar = new StringBuilder("[");
        for (int i = 0; i < filledBars; i++) {
            progressBar.append("#");
        }
        for (int i = filledBars; i < totalBars; i++) {
            progressBar.append("\u2022");
        }
        progressBar.append("]");
        return progressBar.toString();
    }

    public String getAdditionSummary() {
        return getSummary();
    }

    @Override
    public String getSummary() {
        String deadlineStr = (deadline != null) ? "by " + deadline : "no deadline";
        return String.format("Goal #%d added: %s - PHP %,.2f %s", getId(), name, goalAmount, deadlineStr);
    }
}