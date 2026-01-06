package com.dam.demo.entity;

import com.dam.framework.annotations.Column;
import com.dam.framework.annotations.Entity;
import com.dam.framework.annotations.GeneratedValue;
import com.dam.framework.annotations.GenerationType;
import com.dam.framework.annotations.Id;
import com.dam.framework.annotations.Table;

/**
 * Demo entity using IDENTITY generation strategy (AUTO_INCREMENT).
 * <p>
 * This is the most common strategy, suitable for:
 * <ul>
 * <li>MySQL (AUTO_INCREMENT)</li>
 * <li>SQL Server (IDENTITY)</li>
 * <li>PostgreSQL (SERIAL/IDENTITY)</li>
 * </ul>
 */
@Entity
@Table(name = "products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "price")
  private Double price;

  // Constructors
  public Product() {
  }

  public Product(String name, Double price) {
    this.name = name;
    this.price = price;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
  }
}
