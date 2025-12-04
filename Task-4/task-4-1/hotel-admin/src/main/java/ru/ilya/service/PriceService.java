package ru.ilya.service;

import java.util.List;


public interface PriceService {
	List<String> getRoomsAndServices(String orderBy);
}
