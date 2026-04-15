package ru.ilya.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ru.ilya.dto.GuestRequest;
import ru.ilya.dto.GuestResponse;
import ru.ilya.exceptions.GuestException;
import ru.ilya.exceptions.NotFoundException;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.GuestService;

@ExtendWith(MockitoExtension.class)
class GuestControllerTest {

    @Mock
    private GuestService guestService;

    @InjectMocks
    private GuestController guestController;

    private static Room room(int number) {
        Room room = new Room();
        room.setNumber(number);
        room.setPrice(2500);
        room.setCapacity(2);
        room.setStars(4);
        return room;
    }

    private static Guest guest(Integer id, String name, Room room, LocalDate from, LocalDate to) {
        Guest guest = new Guest(name, room, from, to);
        guest.setId(id);
        return guest;
    }

    private static GuestResponse toResponse(Guest guest) {
        GuestResponse response = new GuestResponse();
        response.setId(guest.getId());
        response.setName(guest.getName());
        if (guest.getRoom() != null) {
            response.setRoomNumber(guest.getRoom().getNumber());
        }
        response.setCheckInDate(guest.getCheckInDate());
        response.setCheckOutDate(guest.getCheckOutDate());
        response.setServices(guest.getServices().stream().map(Service::getName).toList());
        return response;
    }

    @Test
    void getAll_returnsGuests() {
        Room room = room(101);
        List<Guest> guests = List.of(
                guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5)),
                guest(2, "Anna", room, LocalDate.of(2025, 5, 2), LocalDate.of(2025, 5, 6)));
        when(guestService.getAllGuests()).thenReturn(guests);

        ResponseEntity<List<GuestResponse>> response = guestController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<GuestResponse> expectedBody = guests.stream()
                .map(GuestControllerTest::toResponse)
                .toList();
        assertEquals(expectedBody, response.getBody());
    }

    @Test
    void getAll_emptyList_returnsEmptyBody() {
        when(guestService.getAllGuests()).thenReturn(List.of());

        ResponseEntity<List<GuestResponse>> response = guestController.getAll();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getById_existingGuest_returnsGuestResponse() {
        Room room = room(101);
        Guest guest = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        when(guestService.findGuestById(1)).thenReturn(guest);

        ResponseEntity<GuestResponse> response = guestController.getById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(toResponse(guest), response.getBody());
    }

    @Test
    void getById_notFound_throwsNotFoundException() {
        when(guestService.findGuestById(99)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> guestController.getById(99));
    }

    @Test
    void checkInGuest_successReturnsGuestResponse() {
        GuestRequest request = new GuestRequest();
        request.setName("Ivan");
        request.setRoomNumber(101);
        request.setCheckInDate(LocalDate.of(2025, 5, 1));
        request.setCheckOutDate(LocalDate.of(2025, 5, 5));

        Room room = room(101);
        Guest savedGuest = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        when(guestService.checkIn("Ivan", 101, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5)))
                .thenReturn(savedGuest);

        ResponseEntity<GuestResponse> response = guestController.checkInGuest(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(toResponse(savedGuest), response.getBody());
        verify(guestService).checkIn("Ivan", 101, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
    }

    @Test
    void checkInGuest_serviceReturnedNull_throwsGuestException() {
        GuestRequest request = new GuestRequest();
        request.setName("Ivan");
        request.setRoomNumber(101);
        request.setCheckInDate(LocalDate.of(2025, 5, 1));
        request.setCheckOutDate(LocalDate.of(2025, 5, 5));

        when(guestService.checkIn(anyString(), anyInt(), any(LocalDate.class), any(LocalDate.class))).thenReturn(null);

        assertThrows(GuestException.class, () -> guestController.checkInGuest(request));
    }

    @Test
    void checkOut_successReturnsOk() {
        when(guestService.checkOut(1)).thenReturn(true);

        ResponseEntity<Void> response = guestController.checkOut(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(guestService).checkOut(1);
    }

    @Test
    void checkOut_serviceReturnedFalse_throwsNotFound() {
        when(guestService.checkOut(1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> guestController.checkOut(1));
    }

    @Test
    void addServiceToGuest_successReturnsOk() {
        when(guestService.addServiceToGuest(1, 2)).thenReturn(true);

        ResponseEntity<Void> response = guestController.addServiceToGuest(1, 2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(guestService).addServiceToGuest(1, 2);
    }

    @Test
    void addServiceToGuest_serviceReturnedFalse_throwsNotFound() {
        when(guestService.addServiceToGuest(1, 2)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> guestController.addServiceToGuest(1, 2));
    }

    @Test
    void removeServiceFromGuest_successReturnsNoContent() {
        when(guestService.removeServiceFromGuest(1, 2)).thenReturn(true);

        ResponseEntity<Void> response = guestController.removeServiceFromGuest(1, 2);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(guestService).removeServiceFromGuest(1, 2);
    }

    @Test
    void removeServiceFromGuest_serviceReturnedFalse_throwsNotFound() {
        when(guestService.removeServiceFromGuest(1, 2)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> guestController.removeServiceFromGuest(1, 2));
    }
}