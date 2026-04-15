package ru.ilya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.ilya.model.Guest;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;

import ru.ilya.exceptions.NotFoundException;
import ru.ilya.dto.GuestRequest;
import ru.ilya.dto.GuestResponse;
import ru.ilya.exceptions.GuestException;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;

    @Autowired
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public ResponseEntity<List<GuestResponse>> getAll() {
        List<GuestResponse> body = guestService.getAllGuests().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponse> getById(@PathVariable Integer id) {
        Guest guest = guestService.findGuestById(id);
        if (guest == null) {
            throw new NotFoundException("Guest not found");
        }
        return ResponseEntity.ok(toResponse(guest));
    }

    @PostMapping
    public ResponseEntity<GuestResponse> checkInGuest(@RequestBody GuestRequest request) {  //должна висеть @Valid для проверки тела запроса
        Guest saved = guestService.checkIn(
                request.getName(),
                request.getRoomNumber(),
                request.getCheckInDate(),
                request.getCheckOutDate());

        if (saved == null) {
            throw new GuestException("Guest check-in failed");
        }

        return ResponseEntity.ok(toResponse(saved));
    }

    @PostMapping("/{guestId}/services/{serviceId}")
    public ResponseEntity<Void> addServiceToGuest(@PathVariable Integer guestId,
            @PathVariable Integer serviceId) {
        boolean success = guestService.addServiceToGuest(guestId, serviceId);
        if (!success) {
            throw new NotFoundException("Guest or service not found");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> checkOut(@PathVariable Integer id) {
        boolean success = guestService.checkOut(id);
        if (!success) {
            throw new NotFoundException("Guest not found");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{guestId}/services/{serviceId}")
    public ResponseEntity<Void> removeServiceFromGuest(@PathVariable Integer guestId,
            @PathVariable Integer serviceId) {
        boolean success = guestService.removeServiceFromGuest(guestId, serviceId);
        if (!success) {
            throw new NotFoundException("Guest or service not found");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private GuestResponse toResponse(Guest guest) {
        GuestResponse response = new GuestResponse();
        response.setId(guest.getId());
        response.setName(guest.getName());

        if (guest.getRoom() != null) {
            response.setRoomNumber(guest.getRoom().getNumber());
        }

        response.setCheckInDate(guest.getCheckInDate());
        response.setCheckOutDate(guest.getCheckOutDate());

        List<String> services = guest.getServices().stream()
                .map(Service::getName)
                .toList();
        response.setServices(services);

        return response;
    }
}