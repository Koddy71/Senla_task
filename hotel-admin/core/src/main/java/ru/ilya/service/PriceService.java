package ru.ilya.service;

import java.util.List;

import ru.ilya.model.Priceable;

public interface PriceService {
    List<Priceable> getRoomsAndServices(String orderBy);
}
