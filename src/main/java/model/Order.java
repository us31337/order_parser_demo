package model;

import enums.MyCurrency;

import java.util.Objects;

public class Order {
    private Integer orderId;
    private Double amount;
    private MyCurrency currency;
    private String comment;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public MyCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(MyCurrency currency) {
        this.currency = currency;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) &&
                Objects.equals(amount, order.amount) &&
                currency == order.currency &&
                Objects.equals(comment, order.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, amount, currency, comment);
    }
}
