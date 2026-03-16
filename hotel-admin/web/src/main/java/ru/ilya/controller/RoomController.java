package ru.ilya.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

import ru.ilya.exceptions.NotFoundException;
import ru.ilya.exceptions.RoomException;
import ru.ilya.exceptions.ValidationException;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAll(@RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(roomService.getRoomsSorted(sortBy));
    }

    @PostMapping
    public ResponseEntity<Room> addRoom(@RequestBody Room room) {
        boolean ok;
        try {
            ok = roomService.addRoom(room);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new RoomException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!ok) {
            throw new RoomException("Не удалось добавить комнату");
        }
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<Void> removeRoom(@PathVariable int number) {
        boolean deleted;
        try {
            deleted = roomService.removeRoom(number);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new RoomException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!deleted) {
            throw new NotFoundException("Комната с номером " + number + " не найдена");
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{number}/status")
    public ResponseEntity<Void> changeRoomStatus(@PathVariable int number, @RequestParam RoomStatus newStatus) {
        boolean changed;
        try {
            changed = roomService.changeStatus(number, newStatus);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new RoomException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!changed) {
            throw new NotFoundException("Комната с номером " + number + " не найдена для изменения статуса");
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{number}/price")
    public ResponseEntity<Void> changeRoomPrice(@PathVariable int number, @RequestParam int price) {
        if (price <= 0) {
            throw new ValidationException("Price must be positive");
        }

        boolean changed;
        try {
            changed = roomService.changePrice(number, price);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new RoomException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!changed) {
            throw new NotFoundException("Комната с номером " + number + " не найдена для изменения цены");
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/free")
    public ResponseEntity<List<Room>> getFreeRooms() {
        return ResponseEntity.ok(roomService.getFreeRooms());
    }

    @GetMapping("/freeByDate")
    public ResponseEntity<List<Room>> getRoomsFreeByDate(@RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(roomService.getRoomsFreeByDate(date));
    }
}