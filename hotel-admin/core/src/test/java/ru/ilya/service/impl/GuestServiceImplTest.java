package ru.ilya.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import ru.ilya.dao.jpa.GuestDaoJpa;
import ru.ilya.model.Guest;
import ru.ilya.model.Room;
import ru.ilya.model.Service;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

@ExtendWith(MockitoExtension.class)
class GuestServiceImplTest {

    @Mock
    private RoomService roomService;

    @Mock
    private ServiceManager serviceManager;

    @Mock
    private AppConfig appConfig;

    @Mock
    private GuestDaoJpa guestDao;

    @InjectMocks
    private GuestServiceImpl guestService;

    private static Room room(int number, int price) {
        Room room = new Room();
        room.setNumber(number);
        room.setPrice(price);
        room.setCapacity(2);
        room.setStars(4);
        return room;
    }

    private static Guest guest(Integer id, String name, Room room, LocalDate from, LocalDate to) {
        Guest guest = new Guest(name, room, from, to);
        guest.setId(id);
        return guest;
    }

    private static Service service(Integer id, String name, int price) {
        Service service = new Service(name, price);
        service.setId(id);
        return service;
    }

    @Test
    void checkIn_success() {
        Room room = room(101, 2500);
        when(roomService.findRoom(101)).thenReturn(room);
        when(appConfig.getRoomHistoryLimit()).thenReturn(10);
        when(guestDao.create(any(Guest.class))).thenAnswer(invocation -> {
            Guest created = invocation.getArgument(0);
            created.setId(1);
            return created;
        });

        Guest result = guestService.checkIn(
                "Ivan",
                101,
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 5));

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Ivan", result.getName());
        assertEquals(1, room.getStayHistory().size());
        assertSame(result, room.getStayHistory().get(0));
        verify(guestDao).create(any(Guest.class));
    }

    @Test
    void checkIn_invalidInput_returnsNull() {
        assertNull(guestService.checkIn(null, 101, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5)));
        assertNull(guestService.checkIn("Ivan", 101, LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 5)));
        verifyNoInteractions(roomService, guestDao);
    }

    @Test
    void checkIn_roomBusy_returnsNull() {
        Room room = room(101, 2500);
        room.addStay(guest(10, "Other", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5)));
        when(roomService.findRoom(101)).thenReturn(room);

        Guest result = guestService.checkIn(
                "Ivan",
                101,
                LocalDate.of(2025, 5, 2),
                LocalDate.of(2025, 5, 4));

        assertNull(result);
        verify(guestDao, never()).create(any(Guest.class));
    }

    @Test
    void checkOut_success() {
        Room room = room(101, 2500);
        Guest existing = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        when(guestDao.findById(1)).thenReturn(existing);
        when(guestDao.delete(1)).thenReturn(true);

        boolean result = guestService.checkOut(1);

        assertTrue(result);
        verify(guestDao).delete(1);
    }

    @Test
    void checkOut_guestMissing_returnsFalse() {
        when(guestDao.findById(1)).thenReturn(null);

        boolean result = guestService.checkOut(1);

        assertFalse(result);
        verify(guestDao, never()).delete(anyInt());
    }

    @Test
    void getAllGuests_returnsDaoList() {
        Room room = room(101, 2500);
        List<Guest> guests = List.of(
                guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5)),
                guest(2, "Anna", room, LocalDate.of(2025, 5, 6), LocalDate.of(2025, 5, 7)));
        when(guestDao.findAll()).thenReturn(guests);

        List<Guest> result = guestService.getAllGuests();

        assertEquals(guests, result);
    }

    @Test
    void getAllGuests_emptyList_returnsEmpty() {
        when(guestDao.findAll()).thenReturn(List.of());

        List<Guest> result = guestService.getAllGuests();

        assertEquals(List.of(), result);
    }

    @Test
    void getGuestsSorted_checkoutDateSortsDescending() {
        Room room = room(101, 2500);
        Guest g1 = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        Guest g2 = guest(2, "Anna", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 7));
        Guest g3 = guest(3, "Boris", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 3));
        when(guestDao.findAll()).thenReturn(List.of(g1, g2, g3));

        List<Guest> result = guestService.getGuestsSorted("checkoutDate");

        assertEquals(List.of(g2, g1, g3), result);
    }

    @Test
    void getGuestsSorted_unknownSortBy_defaultsToNameSort() {
        Room room = room(101, 2500);
        Guest g1 = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        Guest g2 = guest(2, "Anna", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 7));
        Guest g3 = guest(3, "Boris", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 3));
        when(guestDao.findAll()).thenReturn(List.of(g1, g2, g3));

        List<Guest> result = guestService.getGuestsSorted("anything-else");

        assertEquals(List.of(g2, g3, g1), result);
    }

    @Test
    void getGuestCount_returnsSize() {
        Room room = room(101, 2500);
        when(guestDao.findAll()).thenReturn(List.of(
                guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5)),
                guest(2, "Anna", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 7))));

        int count = guestService.getGuestCount();

        assertEquals(2, count);
    }

    @Test
    void getGuestCount_emptyList_returnsZero() {
        when(guestDao.findAll()).thenReturn(List.of());

        int count = guestService.getGuestCount();

        assertEquals(0, count);
    }

    @Test
    void findGuestById_found() {
        Room room = room(101, 2500);
        Guest existing = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        when(guestDao.findById(1)).thenReturn(existing);

        Guest result = guestService.findGuestById(1);

        assertEquals(existing, result);
    }

    @Test
    void findGuestById_notFound_returnsNull() {
        when(guestDao.findById(1)).thenReturn(null);

        Guest result = guestService.findGuestById(1);

        assertNull(result);
    }

    @Test
    void addServiceToGuest_success() {
        Room room = room(101, 2500);
        Guest existing = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        Service spa = service(2, "Spa", 500);
        when(guestDao.findById(1)).thenReturn(existing);
        when(serviceManager.findService(2)).thenReturn(spa);
        when(guestDao.update(existing)).thenReturn(existing);

        boolean result = guestService.addServiceToGuest(1, 2);

        assertTrue(result);
        assertTrue(existing.getServices().contains(spa));
        verify(guestDao).update(existing);
    }

    @Test
    void addServiceToGuest_missingGuestOrService_returnsFalse() {
        when(guestDao.findById(1)).thenReturn(null);
        when(serviceManager.findService(2)).thenReturn(service(2, "Spa", 500));

        boolean result = guestService.addServiceToGuest(1, 2);

        assertFalse(result);
        verify(guestDao, never()).update(any(Guest.class));
    }

    @Test
    void addServiceToGuest_alreadyAdded_returnsFalse() {
        Room room = room(101, 2500);
        Guest existing = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        Service spa = service(2, "Spa", 500);
        existing.addService(spa);
        when(guestDao.findById(1)).thenReturn(existing);
        when(serviceManager.findService(2)).thenReturn(spa);

        boolean result = guestService.addServiceToGuest(1, 2);

        assertFalse(result);
    }

    @Test
    void removeServiceFromGuest_success() {
        Room room = room(101, 2500);
        Guest existing = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        Service spa = service(2, "Spa", 500);
        existing.addService(spa);
        when(guestDao.findById(1)).thenReturn(existing);
        when(serviceManager.findService(2)).thenReturn(spa);
        when(guestDao.update(existing)).thenReturn(existing);

        boolean result = guestService.removeServiceFromGuest(1, 2);

        assertTrue(result);
        assertFalse(existing.getServices().contains(spa));
        verify(guestDao).update(existing);
    }

    @Test
    void removeServiceFromGuest_missingGuestOrService_returnsFalse() {
        when(guestDao.findById(1)).thenReturn(null);
        when(serviceManager.findService(2)).thenReturn(service(2, "Spa", 500));

        boolean result = guestService.removeServiceFromGuest(1, 2);

        assertFalse(result);
        verify(guestDao, never()).update(any(Guest.class));
    }

    @Test
    void removeServiceFromGuest_serviceNotAttached_returnsFalse() {
        Room room = room(101, 2500);
        Guest existing = guest(1, "Ivan", room, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 5));
        Service spa = service(2, "Spa", 500);
        when(guestDao.findById(1)).thenReturn(existing);
        when(serviceManager.findService(2)).thenReturn(spa);

        boolean result = guestService.removeServiceFromGuest(1, 2);

        assertFalse(result);
    }
}
