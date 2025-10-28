import model.Room;
import model.RoomStatus;
import model.Service;
import util.RoomService;
import util.ServiceManager;
import util.impl.RoomServiceImpl;
import util.impl.ServiceManagerImpl;

public class HotelAdmin {

	public static void main(String[] args) {

		RoomService roomService = new RoomServiceImpl();
		ServiceManager serviceManager = new ServiceManagerImpl();

		roomService.addRoom(new Room(101, 1500));
		roomService.addRoom(new Room(102, 2000));

		serviceManager.addService(new Service("Завтрак", 300));
		serviceManager.addService(new Service("SPA", 1200));

		roomService.checkIn(101);

		roomService.changeStatus(102, RoomStatus.MAINTENANCE);

		serviceManager.changePrice("SPA", 1000);

		System.out.println("--- Номера ---");
		for (Room room : roomService.getAllRooms()) {
			System.out.println(room.getInfo());
		}

		System.out.println("--- Услуги ---");
		for (Service service : serviceManager.getAllServices()) {
			System.out.println(service.getInfo());
		}
	}
}
