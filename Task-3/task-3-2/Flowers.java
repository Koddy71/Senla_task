import java.util.ArrayList;
import java.util.List;

abstract class Flower {
	private String name;
	private int price;

	public Flower(String name, int price) {
		this.name = name;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public abstract String getColor();
}

class Rose extends Flower {
	public Rose() {
		super("Роза", 200);
	}

	@Override
	public String getColor() {
		return "Красный";
	}
}

class Tulip extends Flower {
	public Tulip() {
		super("Тюльпан", 100);
	}

	@Override
	public String getColor() {
		return "Жёлтый";
	}
}

class Pion extends Flower {
	public Pion() {
		super("Пион", 150);
	}

	@Override
	public String getColor() {
		return "Розовый";
	}
}

class Bouquet {
	private List<Flower> flowers;
	public Bouquet() {
		flowers = new ArrayList<>();
	}

	public void addFlower(Flower flower) {
		flowers.add(flower);
	}

	public int getTotalPrice() {
		int price = 0;
		for (Flower f : flowers) {
			price += f.getPrice();
		}
		return price;
	}

	public void showBouquet() {
		System.out.println("Ваш букет стоимостью: " + getTotalPrice());
		for (Flower f : flowers) {
			System.out.println(f.getName());
		}

	}
}

public class Flowers {
	public static void main(String[] s) {
		Bouquet bouquet = new Bouquet();
		bouquet.addFlower(new Rose());
		bouquet.addFlower(new Tulip());
		bouquet.addFlower(new Pion());

		bouquet.showBouquet();
	}

}