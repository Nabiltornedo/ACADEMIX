package com.academix.schedule.dto;

import com.academix.schedule.entity.RoomType;
import com.academix.schedule.entity.ScheduleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class ScheduleDtos {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateScheduleRequest {
        private Long courseId;
        private Long teacherId;
        private Long roomId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate endDate;
        private ScheduleType type;
        private boolean isRecurring;
        private String academicYear;
        private String semester;

        public boolean getIsRecurring() {
            return isRecurring;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ScheduleResponse {
        private Long id;
        private Long courseId;
        private Long teacherId;
        private Long roomId;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDate startDate;
        private LocalDate endDate;
        private ScheduleType type;
        private boolean isRecurring;
        private String academicYear;
        private String semester;
        private LocalDateTime createdAt;

        public boolean getIsRecurring() {
            return isRecurring;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateRoomRequest {
        private String roomCode;
        private String name;
        private String building;
        private Integer floor;
        private Integer capacity;
        private RoomType type;
        private boolean hasProjector;
        private boolean hasComputer;

        public boolean getHasProjector() {
            return hasProjector;
        }

        public boolean getHasComputer() {
            return hasComputer;
        }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRoomRequest {
        private String name;
        private String building;
        private Integer floor;
        private Integer capacity;
        private RoomType type;
        private Boolean hasProjector;
        private Boolean hasComputer;
        private Boolean isAvailable;

        public Boolean getHasProjector() { return hasProjector; }
        public Boolean getHasComputer() { return hasComputer; }
        public Boolean getIsAvailable() { return isAvailable; }
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RoomResponse {
        private Long id;
        private String roomCode;
        private String name;
        private String building;
        private Integer floor;
        private Integer capacity;
        private RoomType type;
        private boolean hasProjector;
        private boolean hasComputer;
        private boolean isAvailable;

        public boolean getHasProjector() {
            return hasProjector;
        }

        public boolean getHasComputer() {
            return hasComputer;
        }

        public boolean getIsAvailable() {
            return isAvailable;
        }
    }
}