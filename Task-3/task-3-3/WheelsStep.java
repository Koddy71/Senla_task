public class WheelsStep implements ILineStep {
	@Override
	public IProductPart buildProductPart() {
		System.out.println("Building Wheels...");
		return new Wheels();
	}
}
