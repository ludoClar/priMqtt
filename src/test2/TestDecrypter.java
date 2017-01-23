package test2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

/*public static test decAES()
{
	Cipher cipher = Cipher.getInstance("AES");
	//cipher.init(Cipher.DECRYPT_MODE, key);
	String decrypted = new String(cipher.doFinal(encrypted));
	System.err.println("Decrypted: " + decrypted);

}*/

public class TestDecrypter
{
	LoraDecrypter decrypter;
	// Test valeur mr Singh -> QM+JzAEAlQACHdWSp8h8cijfzWVX2Ky5tsHzOSI=
	//QM+JzAEAAgACpSvq0ctZNSVmNLdv7xaF4AZawjg=
	public static String[] data = { "QM+JzAEAlQACHdWSp8h8cijfzWVX2Ky5tsHzOSI=" };

	public TestDecrypter() throws Exception
	{
		decrypter = new LoraDecrypter(Hex.decode("2B7E151628AED2A6ABF7158809CF4F3C"));
		//decrypter = new LoraDecrypter(Hex.decode("3C4FCF098815F7ABA6D2AE2816157E2B"));
	}

	public static void main(String[] args) throws Exception
	{
		TestDecrypter t = new TestDecrypter();
		for (int i = 0; i < data.length; i++)
		{
			byte[] decrypt = t.getDecrypter().decrypt(data[i]);
			String res = t.convertToHexa(decrypt);
			//String[] tabRes = new String[res.length() / 2];
			//int b = 0;
			//			for (int a = 0; a < tabRes.length; a += 2)
			//			{
			//				tabRes[b++] = String.valueOf(res.charAt(a) + res.charAt(a + 1));
			//			}
			/* for (int a = 0; a < tabRes.length; a++)
			 * System.out.print(tabRes[a]); System.out.println(); */
			InterpreteData(decodeData(decrypt));
		}
	}

	public static boolean InterpreteData(List<String> datas)
	{
		if (datas.size() != 16)
			return false;
		// Byte[0] -> LED
		int led = Integer.parseInt(datas.get(0), 16);

		// Byte[1]+Byte[2] -> Pression atmospherique;
		int pressure = Integer.parseInt(datas.get(2) + datas.get(1), 16);

		// Byte[3]+Byte[4] -> Temperature
		int temperature = Integer.parseInt(datas.get(3) + datas.get(4), 16);

		//Byte[5]+Byte[6] -> Altitude
		int altitude = Integer.parseInt(datas.get(6) + datas.get(5), 16);
		//System.out.println("altitude : " + altitude);

		//Byte[7] -> Battery
		int battery = Integer.parseInt(datas.get(7), 16);

		//Byte[8]+Byte[9]+Byte[10] -> Latitude
		int latitude = Integer.parseInt(datas.get(8) + datas.get(9) + datas.get(10), 16);
		//String latInBin = Integer.toBinaryString(latitude);
		String data = datas.get(8) + datas.get(9) + datas.get(10);
		String val = new BigInteger(data, 16).toString(2);
		String resLat = "";
		if (val.charAt(0) == '1')
			resLat += "+";
		else
			resLat += "-";
		int decimal = Integer.parseInt(val.substring(1), 2);
		System.out.println("dec : " + decimal);
		float resTemp = ((float) (90 * decimal) / ((float) 8388608));

		resLat += resTemp;
		System.out.println();
		System.out.println("latitude : " + resLat);

		//Byte[11]+Byte[12]+Byte[13] -> Latitude
		int longitude = Integer.parseInt(datas.get(13) + datas.get(12) + datas.get(11), 16);

		//Byte[5]+Byte[6] -> Altitude
		int altitude2 = Integer.parseInt(datas.get(14) + datas.get(15), 16);
		System.out.println("altitude : " + altitude2);
		return true;
	}

	public static List<String> decodeData(byte[] datas)
	{
		String field = "";
		String res = "";
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < datas.length - 4; i++)
		{
			byte[] data = { datas[i] };
			field = TestDecrypter.convertToHexa(data);
			//System.out.print(field);
			for (int a = 0; a < field.length(); a += 2)
			{
				result.add("" + field.charAt(a) + field.charAt(a + 1));
			}
			System.out.print(result);
			//			int value = new BigInteger(field, 16).intValue();
			//			res += value + " ";
		}
		return result;
		//System.out.println(res);
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
