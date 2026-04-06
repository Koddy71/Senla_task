package ru.ilya.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.ilya.autoconfig.AppConfig;
import ru.ilya.dao.jpa.RoomDaoJpa;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private RoomDaoJpa roomDao;

    @InjectMocks
    private RoomServiceImpl roomService;

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
    void addRoom_success() {
        Room room = room(101, 2500, 2, 4);
        when(roomDao.findById(101)).thenReturn(null);
        when(roomDao.create(room)).thenReturn(room);

        boolean result = roomService.addRoom(room);

        assertTrue(result);
        verify(roomDao).create(room);
    }

    @Test
    void addRoom_null_returnsFalse() {
        boolean result = roomService.addRoom(null);

        assertFalse(result);
        verifyNoInteractions(roomDao);
    }

    @Test
    void removeRoom_success() {
        when(roomDao.delete(101)).thenReturn(true);

        boolean result = roomService.removeRoom(101);

        assertTrue(result);
        verify(roomDao).delete(101);
    }

    @Test
    void removeRoom_notFound_returnsFalse() {
        when(roomDao.delete(101)).thenReturn(false);

        boolean result = roomService.removeRoom(101);

        assertFalse(result);
    }

    @Test
    void findRoom_found() {
        Room room = room(101, 2500, 2, 4);
        when(roomDao.findById(101)).thenReturn(room);

        Room result = roomService.findRoom(101);

        assertEquals(room, result);
    }

    @Test
    void findRoom_notFound_returnsNull() {
        when(roomDao.findById(101)).thenReturn(null);

        Room result = roomService.findRoom(101);

        assertNull(result);
    }

    @Test
    void changeStatus_success() {
        Room room = room(101, 2500, 2, 4);
        when(appConfig.isRoomStatusChangeEnable()).thenReturn(true);
        when(roomDao.findById(101)).thenReturn(room);
        when(roomDao.update(room)).thenReturn(room);

        boolean result = roomService.changeStatus(101, RoomStatus.MAINTENANCE);

        assertTrue(result);
        assertEquals(RoomStatus.MAINTENANCE, room.getStatus());
        verify(roomDao).update(room);
    }

    @Test
    void changeStatus_disabledInConfig_returnsFalse() {
        when(appConfig.isRoomStatusChangeEnable()).thenReturn(false);

        boolean result = roomService.changeStatus(101, RoomStatus.MAINTENANCE);

        assertFalse(result);
        verifyNoInteractions(roomDao);
    }

    @Test
    void changePrice_success() {
        Room room = room(101, 2500, 2, 4);
        when(roomDao.findById(101)).thenReturn(room);
        when(roomDao.update(room)).thenReturn(room);

        boolean result = roomService.changePrice(101, 3000);

        assertTrue(result);
        assertEquals(3000, room.getPrice());
        verify(roomDao).update(room);
    }

    @Test
    void changePrice_invalidPrice_returnsFalse() {
        Room room = room(101, 2500, 2, 4);
        when(roomDao.findById(101)).thenReturn(room);

        boolean result = roomService.changePrice(101, 0);

        assertFalse(result);
        verify(roomDao, never()).update(any(Room.class));
    }

    @Test
    void getAllRooms_returnsDaoList() {
        Room room1 = room(101, 2500, 2, 4);
        Room room2 = room(102, 1500, 1, 3);
        when(roomDao.findAll()).thenReturn(List.of(room1, room2));

        List<Room> result = roomService.getAllRooms();

        assertEquals(List.of(room1, room2), result);
    }

    @Test
    void getAllRooms_emptyList_returnsEmpty() {
        when(roomDao.findAll()).thenReturn(List.of());

        List<Room> result = roomService.getAllRooms();

        assertEquals(List.of(), result);
    }

    @Test
    void getFreeRooms_filtersOnlyFreeRooms() {
        Room freeRoom = org.mockito.Mockito.mock(Room.class);
        Room busyRoom = org.mockito.Mockito.mock(Room.class);
        when(freeRoom.isFreeOn(any(LocalDate.class))).thenReturn(true);
        when(busyRoom.isFreeOn(any(LocalDate.class))).thenReturn(false);
        when(roomDao.findAll()).thenReturn(List.of(freeRoom, busyRoom));

        List<Room> result = roomService.getFreeRooms();

        assertEquals(List.of(freeRoom), result);
    }

    @Test
    void getFreeRooms_noneFree_returnsEmpty() {
        Room room1 = org.mockito.Mockito.mock(Room.class);
        Room room2 = org.mockito.Mockito.mock(Room.class);
        when(room1.isFreeOn(any(LocalDate.class))).thenReturn(false);
        when(room2.isFreeOn(any(LocalDate.class))).thenReturn(false);
        when(roomDao.findAll()).thenReturn(List.of(room1, room2));

        List<Room> result = roomService.getFreeRooms();

        assertEquals(List.of(), result);
    }

    @Test
    void countFreeRooms_countsOnlyFreeRooms() {
        Room room1 = org.mockito.Mockito.mock(Room.class);
        Room room2 = org.mockito.Mockito.mock(Room.class);
        when(room1.isFreeOn(any(LocalDate.class))).thenReturn(true);
        when(room2.isFreeOn(any(LocalDate.class))).thenReturn(false);
        when(roomDao.findAll()).thenReturn(List.of(room1, room2));

        int count = roomService.countFreeRooms();

        assertEquals(1, count);
    }

    @Test
    void countFreeRooms_noneFree_returnsZero() {
        Room room1 = org.mockito.Mockito.mock(Room.class);
        Room room2 = org.mockito.Mockito.mock(Room.class);
        when(room1.isFreeOn(any(LocalDate.class))).thenReturn(false);
        when(room2.isFreeOn(any(LocalDate.class))).thenReturn(false);
        when(roomDao.findAll()).thenReturn(List.of(room1, room2));

        int count = roomService.countFreeRooms();

        assertEquals(0, count);
    }

    @Test
    void getRoomsFreeByDate_filtersByDate() {
        Room freeRoom = org.mockito.Mockito.mock(Room.class);
        Room busyRoom = org.mockito.Mockito.mock(Room.class);
        LocalDate date = LocalDate.of(2025, 6, 10);
        when(freeRoom.isFreeOn(date)).thenReturn(true);
        when(busyRoom.isFreeOn(date)).thenReturn(false);
        when(roomDao.findAll()).thenReturn(List.of(freeRoom, busyRoom));

        List<Room> result = roomService.getRoomsFreeByDate(date);

        assertEquals(List.of(freeRoom), result);
    }

    @Test
    void getRoomsFreeByDate_noneFree_returnsEmpty() {
        Room room1 = org.mockito.Mockito.mock(Room.class);
        Room room2 = org.mockito.Mockito.mock(Room.class);
        LocalDate date = LocalDate.of(2025, 6, 10);
        when(room1.isFreeOn(date)).thenReturn(false);
        when(room2.isFreeOn(date)).thenReturn(false);
        when(roomDao.findAll()).thenReturn(List.of(room1, room2));

        List<Room> result = roomService.getRoomsFreeByDate(date);

        assertEquals(List.of(), result);
    }

    @Test
    void getRoomsSorted_priceSortsDescending() {
        Room room1 = room(101, 1500, 2, 3);
        Room room2 = room(102, 2500, 3, 4);
        Room room3 = room(103, 1000, 1, 5);
        when(roomDao.findAll()).thenReturn(List.of(room1, room2, room3));

        List<Room> result = roomService.getRoomsSorted("price");

        assertEquals(List.of(room2, room1, room3), result);
    }

    @Test
    void getRoomsSorted_unknownSortBy_defaultsToPriceSort() {
        Room room1 = room(101, 1500, 2, 3);
        Room room2 = room(102, 2500, 3, 4);
        Room room3 = room(103, 1000, 1, 5);
        when(roomDao.findAll()).thenReturn(List.of(room1, room2, room3));

        List<Room> result = roomService.getRoomsSorted("anything-else");

        assertEquals(List.of(room2, room1, room3), result);
    }
}
