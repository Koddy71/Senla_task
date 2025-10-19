import java.util.Random;

public class Digit {
	public static int findDigit(int n){
		int max = 0;
		int temp;

		while (n>0){
			temp = n%10;
			if (temp >max) {
				max=temp;
			}
			n/=10;
		}
		return max;
	}

	public static void main(String[] s){
		Random random = new Random();
		int number = random.nextInt(900)+100;
		System.out.println("Сгенерированное число: "+ number);
		System.out.println("Наибольшая цифра: " + findDigit(number));
	}
}
