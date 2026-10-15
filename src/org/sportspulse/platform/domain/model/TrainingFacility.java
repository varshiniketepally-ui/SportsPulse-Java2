package org.sportspulse.platform.domain.model;

import java.io.Serializable;

/**
 * Domain model representing a specialized training facility or sports resource.
 */
public class TrainingFacility implements Serializable {

    public enum FacilityType {
        TACTICAL_PITCH("Tactical Pitch"),
        PERFORMANCE_GYM("High Performance Gym"),
        HYDRO_RECOVERY("Hydrotherapy Pool"),
        BIOMECHANICS_LAB("Biomechanics & Motion Lab"),
        MEDICAL_BAY("Sports Medicine Center");

        private final String displayName;

        FacilityType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private String id;
    private String name;
    private FacilityType type;
    private int capacity;
    private int currentOccupancy;

    public TrainingFacility(String id, String name, FacilityType type, int capacity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.currentOccupancy = 0;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public FacilityType getType() { return type; }
    public void setType(FacilityType type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }

    public boolean allocateSlot() {
        if (currentOccupancy < capacity) {
            currentOccupancy++;
            return true;
        }
        return false;
    }

    public void releaseSlot() {
        if (currentOccupancy > 0) {
            currentOccupancy--;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Cap: %d | Occupancy: %d",
                id, name, type.getDisplayName(), capacity, currentOccupancy);
    }
}
