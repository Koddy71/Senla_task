package ru.ilya.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.ilya.model.Guest;
import ru.ilya.service.GuestService;

import ru.ilya.exceptions.NotFoundException;
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
    public ResponseEntity<List<Guest>> getAll() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping
    public ResponseEntity<Guest> checkInGuest(@RequestBody Guest guest) {
        Guest g;
        try {
            g = guestService.checkIn(guest.getName(), guest.getRoom().getNumber(), guest.getCheckInDate(), guest.getCheckOutDate());
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new GuestException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (g==null) {
            throw new GuestException("Не удалось зарегистрировать заезд");
        }

        return ResponseEntity.ok(guest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> checkOut(@PathVariable int id) {
        boolean ok;
        try {
            ok = guestService.checkOut(id);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new GuestException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!ok) {
            throw new NotFoundException("Гость с id=" + id + " не найден");
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/services/{serviceId}")
    public ResponseEntity<Void> addServiceToGuest(@PathVariable int id, @PathVariable int serviceId) {
        boolean ok;
        try {
            ok = guestService.addServiceToGuest(id, serviceId);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new GuestException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!ok) {
            throw new NotFoundException("Гость или услуга не найдены / не удалось добавить услугу");
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/services/{serviceId}")
    public ResponseEntity<Void> removeServiceFromGuest(@PathVariable int id, @PathVariable int serviceId) {
        boolean ok;
        try {
            ok = guestService.removeServiceFromGuest(id, serviceId);
        } catch (NoSuchMethodError | AbstractMethodError e) {
            throw new GuestException("Нарушение контрактов сервисов: method signature mismatch");
        }

        if (!ok) {
            throw new NotFoundException("Гость или услуга не найдены / не удалось удалить услугу");
        }

        return ResponseEntity.noContent().build();
    }
}