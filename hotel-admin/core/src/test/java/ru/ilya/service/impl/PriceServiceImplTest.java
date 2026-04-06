package ru.ilya.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.ilya.model.Priceable;
import ru.ilya.model.Room;
import ru.ilya.model.RoomStatus;
import ru.ilya.model.Service;
import ru.ilya.service.RoomService;
import ru.ilya.service.ServiceManager;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    private RoomService roomService;

    @Mock
    private ServiceManager serviceManager;

    @InjectMocks
    private PriceServiceImpl priceService;

    private static Room room(int number, int price) {
        Room room = new Room();
        room.setNumber(number);
        room.setPrice(price);
        room.setCapacity(2);
        room.setStars(4);
        room.setStatus(RoomStatus.ACTIVE);
        return room;
    }

    private static Service service(Integer id, String name, int price) {
        Service service = new Service(name, price);
        service.setId(id);
        return service;
    }

    @Test
    void getRoomsAndServices_serviceOrderPutsServicesFirst() {
        Room room1 = room(101, 3000);
        Room room2 = room(102, 1500);
        Service s1 = service(1, "Spa", 700);
        Service s2 = service(2, "Breakfast", 200);
        when(roomService.getAllRooms()).thenReturn(List.of(room1, room2));
        when(serviceManager.getAllServices()).thenReturn(List.of(s1, s2));

        List<Priceable> result = priceService.getRoomsAndServices("service");

        assertEquals(List.of(s2, s1, room2, room1), result);
    }

    @Test
    void getRoomsAndServices_defaultOrderPutsRoomsFirst() {
        Room room1 = room(101, 3000);
        Room room2 = room(102, 1500);
        Service s1 = service(1, "Spa", 700);
        Service s2 = service(2, "Breakfast", 200);
        when(roomService.getAllRooms()).thenReturn(List.of(room1, room2));
        when(serviceManager.getAllServices()).thenReturn(List.of(s1, s2));

        List<Priceable> result = priceService.getRoomsAndServices(null);

        assertEquals(List.of(room2, room1, s2, s1), result);
    }
}
