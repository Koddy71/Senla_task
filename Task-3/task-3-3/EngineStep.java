public class EngineStep implements ILineStep {
	@Override
	public IProductPart buildProductPart(){
		System.out.println("Building Engine...");
		return new Engine();
	}

}
