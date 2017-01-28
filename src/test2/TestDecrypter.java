package test2;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

public class TestDecrypter
{
	LoraDecrypter decrypter;
	//public static String[] data = { "QM+JzAEAlQACHdWSp8h8cijfzWVX2Ky5tsHzOSI=" };
	public static String[] data = { "QM+JzAEAAgACpSvq0ctZNSVmNLdv7xaF4AZawjg=" };

	public TestDecrypter() throws Exception
	{
		decrypter = new LoraDecrypter(Hex.decode("2B7E151628AED2A6ABF7158809CF4F3C"));
	}

	public static void main(String[] args) throws Exception
	{
		TestDecrypter t = new TestDecrypter();
		for (int i = 0; i < data.length; i++)
		{
			byte[] decrypt = t.getDecrypter().decrypt(data[i]);
			String res = t.convertToHexa(decrypt);
			LoraMoteMessage loraMoteMessage = new LoraMoteMessage(decrypt);
			System.out.println("état de la led : " + (loraMoteMessage.getLedState() ? "allumée" : "éteinte"));
			System.out.println("pression atmosphérique : " + loraMoteMessage.getAtmosphericPressure() + "hPA");
			System.out.println("temperature : " + loraMoteMessage.getTemperature() + "°C");
			System.out.println("latitude : " + loraMoteMessage.getLatitude());
			System.out.println("longitude : " + loraMoteMessage.getLongitude());
			System.out.println("altitude : " + loraMoteMessage.getLatitude());
		}
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
