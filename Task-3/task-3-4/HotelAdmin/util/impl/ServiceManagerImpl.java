package util.impl;

import model.Service;
import util.ServiceManager;

import java.util.ArrayList;
import java.util.List;

public class ServiceManagerImpl implements ServiceManager {

	private List<Service> services = new ArrayList<>();

	@Override
	public void addService(Service service) {
		services.add(service);
	}

	@Override
	public Service findService(String name) {
		for (Service s : services) {
			if (s.getName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
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
		return services;
	}
}
