package test2;

import java.math.BigInteger;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

/** Test against the 6 first data transmitted through our antenna
 * 
 * 
 * @author Julien */
public class TestDecrypter
{
	LoraDecrypter decrypter;
	// Test valeur mr Singh -> QM+JzAEAlQACHdWSp8h8cijfzWVX2Ky5tsHzOSI=
	//QM+JzAEAAgACpSvq0ctZNSVmNLdv7xaF4AZawjg=
	public static String[] data = { "QM+JzAEAAgACpSvq0ctZNSVmNLdv7xaF4AZawjg=" };

	public TestDecrypter() throws Exception
	{
		decrypter = new LoraDecrypter(Hex.decode("2B7E151628AED2A6ABF7158809DF4F3C"));
		//decrypter = new LoraDecrypter(Hex.decode("3C4FCF098815F7ABA6D2AE2816157E2B"));
	}

	public static void main(String[] args) throws Exception
	{
		TestDecrypter t = new TestDecrypter();
		for (int i = 0; i < data.length; i++)
		{
			byte[] decrypt = t.getDecrypter().decrypt(data[i]);
			String res = t.convertToHexa(decrypt);
			System.out.println("Données reçues : " + res);
			decodeData(decrypt);
		}
	}

	public static void decodeData(byte[] datas)
	{
		String field = "";
		String res = "";
		for (int i = 0; i < datas.length; i++)
		{
			byte[] data = { datas[i] };
			field = TestDecrypter.convertToHexa(data);
			int value = new BigInteger(field, 16).intValue();
			res += value + " ";
		}
		System.out.println(res);
	}

	public static String convertToHexa(byte[] decrypted)
	{
		return ByteUtils.toHexString(decrypted);
	}

	public LoraDecrypter getDecrypter()
	{
		return decrypter;
	}
}
