public class Main {
	public static void main(String[] s) {
		ILineStep engineStep = new EngineStep();
		ILineStep bodyStep = new BodyStep();
		ILineStep wheelsStep = new WheelsStep();

		IAssemblyLine assemblyLine = new CarAssemblyLine(engineStep, bodyStep, wheelsStep);
		IProduct car = new Car();

		IProduct assembledCar = assemblyLine.assembleProduct(car);
		System.out.println(assembledCar);
	}
}
