import java.math.BigDecimal;

public class Deposit extends TrackerRecord {
    private final int goalID;
    private final String goalName;
    private final BigDecimal amount;
    private final String date;
    private final String note;

    public Deposit(int depositID, int goalID, String goalName, double amount, String date, String note) {
        this(depositID, goalID, goalName, BigDecimal.valueOf(amount), date, note);
    }

    public Deposit(int depositID, int goalID, String goalName, BigDecimal amount, String date, String note) {
        super(depositID);
        this.goalID = goalID;
        this.goalName = goalName;
        this.amount = TrackerInput.positiveAmount(amount);
        this.date = TrackerInput.date(date);
        this.note = note;
    }
    public int getDepositID() {
        return getId();
    }
    public int getGoalID() {
        return goalID;
    }
    public String getGoalName() {
        return goalName;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public String getDate() {
        return date;
    }
    public String getNote() {
        return note;
    }

    @Override
    public String getSummary() {
        String noteSuffix = (note == null || note.isEmpty()) ? "" : " - " + note;
        return String.format("  [%d] PHP %,.2f on %s%s", getId(), amount, date, noteSuffix);
    }
}
