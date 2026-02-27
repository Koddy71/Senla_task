public class BodyStep implements ILineStep {
	@Override
	public IProductPart buildProductPart() {
		System.out.println("Building Body...");
		return new Body();
	}
}
