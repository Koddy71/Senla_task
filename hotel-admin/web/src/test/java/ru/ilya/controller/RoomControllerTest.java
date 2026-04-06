package ru.ilya.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import ru.ilya.exceptions.NotFoundException;
import ru.ilya.exceptions.RoomException;
import ru.ilya.exceptions.ValidationException;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.service.RoomService;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private static Room room(int number, int price, int capacity, int stars) {
        Room room = new Room();
        room.setNumber(number);
        room.setPrice(price);
        room.setCapacity(capacity);
        room.setStars(stars);
        room.setStatus(RoomStatus.ACTIVE);
        return room;
    }

    @Test
    void getAll_returnsRooms() {
        Room room1 = room(101, 3000, 2, 4);
        Room room2 = room(102, 1500, 1, 3);
        when(roomService.getRoomsSorted("price")).thenReturn(List.of(room1, room2));

        ResponseEntity<List<Room>> response = roomController.getAll("price");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(room1, room2), response.getBody());
    }

    @Test
    void getAll_emptyList_returnsEmptyBody() {
        when(roomService.getRoomsSorted(null)).thenReturn(List.of());

        ResponseEntity<List<Room>> response = roomController.getAll(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void addRoom_successReturnsOriginalRoom() {
        Room room = room(101, 3000, 2, 4);
        when(roomService.addRoom(room)).thenReturn(true);

        ResponseEntity<Room> response = roomController.addRoom(room);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(room, response.getBody());
        verify(roomService).addRoom(room);
    }

    @Test
    void addRoom_serviceReturnedFalse_throwsRoomException() {
        Room room = room(101, 3000, 2, 4);
        when(roomService.addRoom(room)).thenReturn(false);

        assertThrows(RoomException.class, () -> roomController.addRoom(room));
    }

    @Test
    void removeRoom_successReturnsNoContent() {
        when(roomService.removeRoom(101)).thenReturn(true);

        ResponseEntity<Void> response = roomController.removeRoom(101);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void removeRoom_serviceReturnedFalse_throwsNotFound() {
        when(roomService.removeRoom(101)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> roomController.removeRoom(101));
    }

    @Test
    void changeRoomStatus_successReturnsOk() {
        when(roomService.changeStatus(101, RoomStatus.MAINTENANCE)).thenReturn(true);

        ResponseEntity<Void> response = roomController.changeRoomStatus(101, RoomStatus.MAINTENANCE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeRoomStatus_serviceReturnedFalse_throwsNotFound() {
        when(roomService.changeStatus(101, RoomStatus.MAINTENANCE)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> roomController.changeRoomStatus(101, RoomStatus.MAINTENANCE));
    }

    @Test
    void changeRoomPrice_successReturnsOk() {
        when(roomService.changePrice(101, 3500)).thenReturn(true);

        ResponseEntity<Void> response = roomController.changeRoomPrice(101, 3500);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeRoomPrice_invalidPrice_throwsValidationException() {
        assertThrows(ValidationException.class, () -> roomController.changeRoomPrice(101, 0));
    }

    @Test
    void changeRoomPrice_serviceReturnedFalse_throwsNotFound() {
        when(roomService.changePrice(101, 3500)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> roomController.changeRoomPrice(101, 3500));
    }

    @Test
    void getFreeRooms_returnsRooms() {
        Room room1 = room(101, 3000, 2, 4);
        Room room2 = room(102, 1500, 1, 3);
        when(roomService.getFreeRooms()).thenReturn(List.of(room1, room2));

        ResponseEntity<List<Room>> response = roomController.getFreeRooms();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(room1, room2), response.getBody());
    }

    @Test
    void getFreeRooms_emptyList_returnsEmptyBody() {
        when(roomService.getFreeRooms()).thenReturn(List.of());

        ResponseEntity<List<Room>> response = roomController.getFreeRooms();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }

    @Test
    void getRoomsFreeByDate_returnsRooms() {
        Room room1 = room(101, 3000, 2, 4);
        Room room2 = room(102, 1500, 1, 3);
        LocalDate date = LocalDate.of(2025, 6, 10);
        when(roomService.getRoomsFreeByDate(date)).thenReturn(List.of(room1, room2));

        ResponseEntity<List<Room>> response = roomController.getRoomsFreeByDate(date);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(room1, room2), response.getBody());
    }

    @Test
    void getRoomsFreeByDate_emptyList_returnsEmptyBody() {
        LocalDate date = LocalDate.of(2025, 6, 10);
        when(roomService.getRoomsFreeByDate(date)).thenReturn(List.of());

        ResponseEntity<List<Room>> response = roomController.getRoomsFreeByDate(date);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
    }
}
