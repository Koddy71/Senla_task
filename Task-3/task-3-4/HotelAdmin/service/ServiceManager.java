package service;

import model.Service;
import java.util.List;

public interface ServiceManager {
	void addService(Service service);

	Service findService(String name);

	boolean changePrice(String name, int newPrice);

	List<Service> getAllServices();
}
