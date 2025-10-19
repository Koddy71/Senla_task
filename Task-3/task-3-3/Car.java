public class Car implements IProduct{
	private IProductPart engine;
	private IProductPart body;
	private IProductPart wheels;
	
	@Override
	public void installFirstPart(IProductPart part){
		this.engine=part;
		System.out.println("Engine installed.");
	}

	@Override
	public void installSecondPart(IProductPart part){
		this.body=part;
		System.out.println("Body installed.");
	}

	@Override
	public void installThirdPart(IProductPart part){
		this.wheels=part;
		System.out.println("Wheels installed.");
	}

	public String toString() {
		return "Car assembled with: " + engine + ", " + body + ", " + wheels;
	}
}
