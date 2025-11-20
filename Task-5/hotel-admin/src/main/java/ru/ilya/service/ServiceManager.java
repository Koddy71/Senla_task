package ru.ilya.service;

import ru.ilya.model.Service;
import java.util.List;

public interface ServiceManager {
	boolean addService(Service service);

	boolean removeService(String name);

	Service findService(String name);

	boolean changePrice(String name, int newPrice);

	List<Service> getAllServices();
}
