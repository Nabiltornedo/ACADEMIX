package com.academix.schedule.repository;

import com.academix.schedule.entity.Room;
import com.academix.schedule.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    boolean existsByRoomCode(String roomCode);

    List<Room> findByIsAvailableTrue();

    List<Room> findByType(RoomType type);

    List<Room> findByBuilding(String building);

    List<Room> findByCapacityGreaterThanEqual(Integer capacity);
}
