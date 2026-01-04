package com.dam.demo.entity;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Entity;
import com.dam.framework.annotations.GeneratedValue;
import com.dam.framework.annotations.GenerationType;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.SequenceGenerator;
import com.dam.framework.annotations.Table;

/**
 * Demo entity using SEQUENCE generation strategy.
 * <p>
 * Best for databases with native sequence support:
 * <ul>
 * <li>PostgreSQL (recommended)</li>
 * <li>Oracle</li>
 * <li>SQL Server 2012+ (has sequences but IDENTITY is more common)</li>
 * </ul>
 * 
 * <p>
 * <b>Note:</b> MySQL does NOT support sequences. Use IDENTITY instead.
 * </p>
 */
@Entity
@Table(name = "orders", schema = "public")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @SequenceGenerator(name = "order_seq", sequenceName = "order_id_seq", allocationSize = 1, initialValue = 1)
  private Long id;

  @Column(name = "order_number", nullable = false)
  private String orderNumber;

  @Column(name = "customer_name")
  private String customerName;

  @Column(name = "total_amount")
  private Double totalAmount;

  // Constructors
  public Order() {
  }

  public Order(String orderNumber, String customerName, Double totalAmount) {
    this.orderNumber = orderNumber;
    this.customerName = customerName;
    this.totalAmount = totalAmount;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public String getCustomerName() {
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  @Override
  public String toString() {
    return "Order{id=" + id +
        ", orderNumber='" + orderNumber + "'" +
        ", customerName='" + customerName + "'" +
        ", totalAmount=" + totalAmount + "}";
  }
}
