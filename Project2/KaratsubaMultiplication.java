import java.math.BigInteger;
import java.util.Random;

//cannot use BigInteger predefined methods for multiplication
//cannot use Strings except in computing appropriate exponent
public class KaratsubaMultiplication
{
	private static final BigInteger MAX_INT_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);

	public static BigInteger karatsuba(final BigInteger factor0, final BigInteger factor1, final int base)
	{
		//base cases
		int n = Math.max(factor0.bitLength(), factor1.bitLength());
		    if (n <= 10){
					long f0 = factor0.longValue();
					long f1 = factor1.longValue();
					long result = f0*f1;
					BigInteger res = BigInteger.valueOf(result);
					return res;
				}
		    // number of bits divided by 2, rounded up
		    n = (n / 2) + (n % 2);
		    BigInteger b = factor0.shiftRight(n);
		    BigInteger a = factor0.subtract(b.shiftLeft(n));
		    BigInteger d = factor1.shiftRight(n);
		    BigInteger c = factor1.subtract(d.shiftLeft(n));
		    // compute sub-expressions
		    BigInteger ac = karatsuba(a, c, base);
		    BigInteger bd = karatsuba(b, d, base);
		    BigInteger abcd = karatsuba(a.add(b), c.add(d), base);

		    return ac.add(abcd.subtract(ac).subtract(bd).shiftLeft(n)).add(bd.shiftLeft(2 * n));
	}
	/*public static int getDigitCount(BigInteger number) {
  double factor = Math.log(2) / Math.log(10);
  int digitCount = (int) (factor * number.bitLength() + 1);
  if (BigInteger.TEN.pow(digitCount - 1).compareTo(number) > 0) {
    return digitCount - 1;
  }
  return digitCount;
}
*/

	public static void main(String[] args)
	{
		//test cases
		if(args.length < 3)
		{
			System.out.println("Need two factors and a base value as input");
			return;
		}
		BigInteger factor0 = null;
		BigInteger factor1 = null;
		final Random r = new Random();
		if(args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("rand") || args[0].equalsIgnoreCase("random"))
		{
			factor0 = new BigInteger(r.nextInt(100000), r);
			System.out.println("First factor : " + factor0.toString());
		}
		else
		{
			factor0 = new BigInteger(args[0]);
		}
		if(args[1].equalsIgnoreCase("r") || args[1].equalsIgnoreCase("rand") || args[1].equalsIgnoreCase("random"))
		{
			factor1 = new BigInteger(r.nextInt(100000), r);
			System.out.println("Second factor : " + factor1.toString());
		}
		else
		{
			factor1 = new BigInteger(args[1]);
		}
		final BigInteger result = karatsuba(factor0, factor1, Integer.parseInt(args[2]));
		System.out.println(result);
		System.out.println(result.equals(factor0.multiply(factor1)));
	}
}
