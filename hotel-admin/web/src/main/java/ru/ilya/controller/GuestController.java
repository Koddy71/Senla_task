package ru.ilya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;
import ru.ilya.dto.GuestDTO;
import ru.ilya.dto.GuestRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private GuestService guestService;

    @Autowired
    public GuestController(GuestService guestService){
        this.guestService = guestService;
    }

    private GuestDTO convertToDto(Guest guest) {
        if (guest == null)
            return null;
        GuestDTO dto = new GuestDTO();
        dto.setId(guest.getId());
        dto.setName(guest.getName());
        if (guest.getRoom() != null)
            dto.setRoomNumber(guest.getRoom().getNumber());
        dto.setCheckInDate(guest.getCheckInDate());
        dto.setCheckOutDate(guest.getCheckOutDate());
        if (guest.getServices() != null) {
            dto.setServiceIds(guest.getServices().stream().map(s -> s.getId()).collect(Collectors.toList()));
        }
        return dto;
    }

    @GetMapping
    public List<GuestDTO> getAllGuests(@RequestParam(required = false) String sortBy) {
        List<Guest> guests;
        if (sortBy != null) {
            guests = guestService.getGuestsSorted(sortBy);
        } else {
            guests = guestService.getAllGuests();
        }
        return guests.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable int id) {
        Guest guest = guestService.findGuestById(id);
        if (guest == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(convertToDto(guest));
    }

    @PostMapping
    public ResponseEntity<GuestDTO> checkInGuest(@RequestBody GuestRequest request) {
        Guest guest = guestService.checkIn(request.getName(), request.getRoomNumber(),
                request.getCheckInDate(), request.getCheckOutDate());
        if (guest == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(guest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> checkOutGuest(@PathVariable int id) {
        if (guestService.checkOut(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{id}/services")
    public ResponseEntity<Void> addServiceToGuest(@PathVariable int id, @RequestParam int serviceId){
        if (guestService.addServiceToGuest(id, serviceId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}/services/{serviceId}")
    public ResponseEntity<Void> removeServiceFromGuest(@PathVariable int id, @PathVariable int serviceId) {
        if (guestService.removeServiceFromGuest(id, serviceId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}