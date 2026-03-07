package ru.ilya.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.ilya.dto.RoomDTO;
import ru.ilya.dto.RoomRequest;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    private RoomDTO convertToDto(Room room) {
        if (room == null)
            return null;
        RoomDTO dto = new RoomDTO();
        dto.setNumber(room.getNumber());
        if (room.getStatus() != null) {
            dto.setStatus(room.getStatus().name());
        } else {
            dto.setStatus(null);
        }
        dto.setPrice(room.getPrice());
        dto.setCapacity(room.getCapacity());
        dto.setStars(room.getStars());
        return dto;
    }

    @GetMapping
    public List<RoomDTO> getAllRooms(@RequestParam(required = false) String sortBy,
            @RequestParam(required = false) LocalDate freeOn) {
        List<Room> rooms;
        if (freeOn != null) {
            rooms = roomService.getRoomsFreeByDate(freeOn);
        } else if (sortBy != null) {
            rooms = roomService.getRoomsSorted(sortBy);
        } else {
            rooms = roomService.getAllRooms();
        }
        return rooms.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{number}")
    public ResponseEntity<RoomDTO> getRoomByNumber(@PathVariable int number){
        Room room = roomService.findRoom(number);
        if (room== null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(convertToDto(room));
    }

    @PostMapping
    public ResponseEntity<Void> addRoom(@RequestBody RoomRequest request){
        Room room = new Room(request.getNumber(), request.getPrice(), request.getCapacity(), request.getStars());
        if (request.getStatus()!=null){
            room.setStatus(RoomStatus.valueOf(request.getStatus()));
        }
        
        if (roomService.addRoom(room)){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{number}")
    public ResponseEntity<Void> removeRoom(@PathVariable int number) {
        if (roomService.removeRoom(number)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{number}/status")
    public ResponseEntity<Void> changeRoomStatus(@PathVariable int number, @RequestParam String status) {
        RoomStatus newStatus;
        try {
            newStatus = RoomStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if(roomService.changeStatus(number, newStatus)){
            return ResponseEntity.ok().build();
        } 
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{number}/price")
    public ResponseEntity<Void> changeRoomPrice(@PathVariable int number, @RequestParam int price) {
        if (roomService.changePrice(number, price)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
