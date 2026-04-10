package ru.ilya.controller;

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
import ru.ilya.service.PriceService;

@ExtendWith(MockitoExtension.class)
class PriceControllerTest {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController priceController;

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
    void getRoomsAndServices_returnsList() {
        Room room = room(101, 3000);
        Service spa = service(1, "Spa", 700);
        when(priceService.getRoomsAndServices("service")).thenReturn(List.of(spa, room));

        List<Priceable> result = priceController.getRoomsAndServices("service");

        assertEquals(List.of(spa, room), result);
    }

    @Test
    void getRoomsAndServices_nullOrderBy_returnsList() {
        Room room = room(101, 3000);
        Service spa = service(1, "Spa", 700);
        when(priceService.getRoomsAndServices(null)).thenReturn(List.of(room, spa));

        List<Priceable> result = priceController.getRoomsAndServices(null);

        assertEquals(List.of(room, spa), result);
    }
}
