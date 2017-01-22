package test1;

import org.bouncycastle.util.encoders.Hex;

/** Test against the 6 first data transmitted through our antenna
 * 
 * 
 * @author Julien */
public class TestDecrypter
{
	static LoraDecrypter decrypter;

	/* String[] data = { "QM+JzAEAAQACtprbMn3/hDWSluTCoioH3+4iZ6c=",
	 * "QM+JzAEAAgACpSsC0R5YGeBmNLdv7xaF4OoKr8o=",
	 * "QM+JzAEAAwACLHRWXIVnww0f0SqrJNPf5w9M91E=",
	 * "QM+JzAEABAACUf3d0tVfljCcMnVZRKh30Jt/0gQ=",
	 * "QM+JzAEABQACz2aOvh2j37Wnc2yMwM3FQlkxdqM=",
	 * "QM+JzAEABgAC9x8hBwAUtk5ETrDesl6kqyErons=" }; */

	/* String[] expected = { "00 25 6e 08 34 0f 1b 8b 00 00 00 00 00 00 ff ff",
	 * "00 25 6f 08 3a 0f 17 88 00 00 00 00 00 00 ff ff",
	 * "00 25 6f 08 46 0f 15 86 00 00 00 00 00 00 ff ff",
	 * "00 25 6f 08 4d 0f 19 86 00 00 00 00 00 00 ff ff",
	 * "00 25 73 08 53 0e e7 85 00 00 00 00 00 00 ff ff",
	 * "00 25 74 08 59 0e ef 84 00 00 00 00 00 00 ff ff" }; */

	public static void setUp() throws Exception
	{
		decrypter = new LoraDecrypter(Hex.decode("2B7E151628AED2A6ABF7158809CF4F3C"));
	}

	public static void main(String[] args) throws Exception
	{
		String[] data = { "QM+JzAEAAgACpSvq0ctZNSVmNLdv7xaF4AZawjg=" };
		setUp();
		for (int i = 0; i < data.length; i++)
		{
			byte[] decrypt = decrypter.decrypt(data[i]);
			System.out.println(decrypt);
		}
	}
}