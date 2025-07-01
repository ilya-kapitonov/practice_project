package com.equipment.tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sports_equipment")
public class SportsEquipment {

    @Id
    @Column(name = "equipment_id", nullable = false, updatable = false)
    private UUID equipmentId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank(message = "Серийный номер не может быть пустым")
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @NotBlank(message = "Название не может быть пустым")
    @Column(name = "equipment_name", nullable = false)
    private String equipmentName;

    @PositiveOrZero(message = "Количество не может быть отрицательным")
    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "condition_id", nullable = false)
    private EquipmentCondition condition;

    @Column(name = "last_check_date")
    private LocalDate lastCheckDate;

    @Column(name = "end_of_service_date", nullable = false)
    private LocalDate endOfServiceDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String description;

    public SportsEquipment() {
        this.equipmentId = UUID.randomUUID();
    }

    public UUID getEquipmentId() {
        return equipmentId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public EquipmentCondition getCondition() {
        return condition;
    }

    public void setCondition(EquipmentCondition condition) {
        this.condition = condition;
    }

    public LocalDate getLastCheckDate() {
        return lastCheckDate;
    }

    public void setLastCheckDate(LocalDate lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
    }

    public LocalDate getEndOfServiceDate() {
        return endOfServiceDate;
    }

    public void setEndOfServiceDate(LocalDate endOfServiceDate) {
        this.endOfServiceDate = endOfServiceDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SportsEquipment{" +
                "equipmentId=" + equipmentId +
                ", category=" + category +
                ", serialNumber='" + serialNumber + '\'' +
                ", quantity=" + quantity +
                ", condition=" + condition +
                ", lastCheckDate=" + lastCheckDate +
                ", endOfServiceDate=" + endOfServiceDate +
                ", createdAt=" + createdAt +
                ", description='" + description + '\'' +
                '}';
    }
}
