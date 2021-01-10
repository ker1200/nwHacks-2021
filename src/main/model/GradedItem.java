package main.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class GradedItem {
    private String name;
    private LocalDateTime date;
    private int weight;

    public GradedItem(String name, LocalDateTime date, int weight) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
