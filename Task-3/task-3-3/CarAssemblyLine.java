public class CarAssemblyLine implements IAssemblyLine{
	private ILineStep firstStep;
	private ILineStep secondStep;
	private ILineStep thirdStep;

	public CarAssemblyLine(ILineStep firstStep, ILineStep secondStep, ILineStep thirdStep) {
		this.firstStep = firstStep;
		this.secondStep = secondStep;
		this.thirdStep = thirdStep;
	}

	@Override
	public IProduct assembleProduct(IProduct product){
		IProductPart part1= firstStep.buildProductPart();
		product.installFirstPart(part1);

		IProductPart part2 = secondStep.buildProductPart();
		product.installSecondPart(part2);

		IProductPart part3 = thirdStep.buildProductPart();
		product.installThirdPart(part3);
		 
		return product;
	}
}
