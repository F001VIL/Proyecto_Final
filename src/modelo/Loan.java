package modelo;

import java.time.LocalDateTime;
import java.util.Objects;

public class Loan {
    public enum ElementType {
        DESKTOP_PC,
        LAPTOP,
        TABLET
    }

    public enum Status {
        PENDING,
        ACTIVE,
        RETURNED,
        OVERDUE,
        CANCELLED
    }

    private Integer id;
    private int userId;
    private ElementType elementType;
    private int elementId;
    private Status status;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String notes;

    public Loan() {
        this.status = Status.PENDING;
        this.loanDate = LocalDateTime.now();
    }

    public Loan(Integer id, int userId, ElementType elementType, int elementId,
                Status status, LocalDateTime loanDate, LocalDateTime dueDate,
                LocalDateTime returnDate, String notes) {
        this.id = id;
        this.userId = userId;
        this.elementType = elementType;
        this.elementId = elementId;
        this.status = status != null ? status : Status.PENDING;
        this.loanDate = loanDate != null ? loanDate : LocalDateTime.now();
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.notes = notes;
    }

    // Basic validation: userId and elementId must be positive and elementType present
    public boolean isValid() {
        return userId > 0 && elementId > 0 && elementType != null && status != null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public int getElementId() {
        return elementId;
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loan loan = (Loan) o;
        return userId == loan.userId &&
                elementId == loan.elementId &&
                Objects.equals(id, loan.id) &&
                elementType == loan.elementType &&
                status == loan.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, elementType, elementId, status);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", userId=" + userId +
                ", elementType=" + elementType +
                ", elementId=" + elementId +
                ", status=" + status +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
