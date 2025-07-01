package com.equipment.tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "equipment_condition")
public class EquipmentCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "condition_id")
    private Integer conditionId;

    @NotBlank(message = "Состояние не может быть пустым")
    @Column(name = "condition_name", nullable = false, unique = true)
    private String conditionName;

    public EquipmentCondition () {}

    public Integer getConditionId() {
        return conditionId;
    }

    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName;
    }

    @Override
    public String toString() {
        return "EquipmentCondition{" +
                "conditionId=" + conditionId +
                ", conditionName='" + conditionName + '\'' +
                '}';
    }
}