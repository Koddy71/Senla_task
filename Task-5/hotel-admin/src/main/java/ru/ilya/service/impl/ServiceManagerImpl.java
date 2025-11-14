package ru.ilya.service.impl;

import ru.ilya.model.Service;
import ru.ilya.service.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ServiceManagerImpl implements ServiceManager {

	private Map<String, Service> services = new HashMap<>();

	@Override
	public boolean addService(Service service) {
		if (service == null || services.containsKey(service.getName())) {
			return false;
		}
		services.put(service.getName(), service);
		return true;
	}

	@Override
	public boolean removeService(String name) {
		return services.remove(name) != null;
	}

	@Override
	public Service findService(String name) {
		return services.get(name);
	}
		
	@Override
	public boolean changePrice(String name, int newPrice) {
		Service service = findService(name);
		if (service != null && newPrice > 0) {
			service.setPrice(newPrice);
			return true;
		}
		return false;
	}

	@Override
	public List<Service> getAllServices() {
		return new ArrayList<>(services.values());
	}
}
