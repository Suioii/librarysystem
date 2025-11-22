package librarysystem;

import java.sql.Timestamp;

public class Hold {
    private int holdId;
    private int bookId;
    private int memberId;
    private Timestamp placeDate;
    private String status; // PENDING, READY, CANCELLED
    private boolean notificationSent;
    private int queuePosition; 

    public Hold(int holdId, int bookId, int memberId,
                Timestamp placeDate, String status,
                boolean notificationSent, int queuePosition) {
        this.holdId = holdId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.placeDate = placeDate;
        this.status = status;
        this.notificationSent = notificationSent;
        this.queuePosition = queuePosition;
    }

    public int getHoldId() { return holdId; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public Timestamp getPlaceDate() { return placeDate; }
    public String getStatus() { return status; }
    public boolean isNotificationSent() { return notificationSent; }
    public int getQueuePosition() { return queuePosition; }

    @Override
    public String toString() {
        return "Hold{" +
                "holdId=" + holdId +
                ", bookId=" + bookId +
                ", memberId=" + memberId +
                ", status='" + status + '\'' +
                ", queuePosition=" + queuePosition +
                '}';
    }
}

