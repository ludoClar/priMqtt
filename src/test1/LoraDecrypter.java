package test1;

import java.util.Arrays;
import java.util.Base64;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.params.KeyParameter;

import test2.TestDecrypter;

/** Decrypt Lora payload from received data.
 * 
 * Not thread safe. Use one instance per thread.
 * 
 * @author Julien */
public class LoraDecrypter
{
	/** AES engine to decrypt message */
	final AESFastEngine aesEngine = new AESFastEngine();
	/** Key of the application */
	KeyParameter key = null;

	public LoraDecrypter(byte[] appKey)
	{
		super();
		key = new KeyParameter(appKey);
		aesEngine.init(true, this.key);
	}

	public static String getAddressDevice(byte[] addr)
	{
		String addressDevice = "";
		for (int i = addr.length - 1; i >= 0; i--)
		{
			byte[] tab = { addr[i] };
			addressDevice += TestDecrypter.convertToHexa(tab);
			if (i > 0)
				addressDevice += ":";
		}
		return addressDevice;
	}

	public byte[] decrypt(String base64encodedData)
	{
		// Decode message
		System.out.println("ok");
		byte[] decode = Base64.getDecoder().decode(base64encodedData);
		System.out.println("step1");
		// Compute sequence number
		System.out.println(decode[7]);
		int sequence = 0;
		System.out.println(String.format("%02X", sequence));
		sequence = (decode[7] * (int) Math.pow(2, 8) + decode[6]);
		System.out.println(String.format("%02X", sequence));
		// Get the adress
		byte[] addrbytes = Arrays.copyOfRange(decode, 1, 5);
		int address = java.nio.ByteBuffer.wrap(addrbytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();

		//System.out.println("Adresse du device : " + addrbytes + " \nnumeroSequence : " + sequence);
		// Move to offset of data
		byte[] payload = new byte[decode.length - 9];
		System.arraycopy(decode, 9, payload, 0, decode.length - 9);
		// decrypt message
		byte[] decrypted = new byte[payload.length];
		decrypthelper(payload, 16, address, 0, 95, decrypted);
		return decrypted;
	}

	private void decrypthelper(byte[] data, int len, int address, int dir, int sequenceCounter, byte[] encBuffer)
	{
		byte[] aBlock = { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };
		byte[] sBlock = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00 };
		int i;
		int size = len;
		byte bufferIndex = 0;
		short ctr = 1;

		aBlock[5] = (byte) dir;

		aBlock[6] = (byte) ((address) & 0xFF);
		aBlock[7] = (byte) ((address >> 8) & 0xFF);
		aBlock[8] = (byte) ((address >> 16) & 0xFF);
		aBlock[9] = (byte) ((address >> 24) & 0xFF);

		aBlock[10] = (byte) ((sequenceCounter) & 0xFF);
		aBlock[11] = (byte) ((sequenceCounter >> 8) & 0xFF);
		aBlock[12] = (byte) ((sequenceCounter >> 16) & 0xFF);
		aBlock[13] = (byte) ((sequenceCounter >> 24) & 0xFF);

		while (size >= 16)
		{
			aBlock[15] = (byte) ((ctr) & 0xFF);
			ctr++;
			aesEngine.processBlock(aBlock, 0, sBlock, 0);
			for (i = 0; i < 16; i++)
			{
				encBuffer[bufferIndex + i] = (byte) (data[bufferIndex + i] ^ sBlock[i]);
			}
			size -= 16;
			bufferIndex += 16;
		}

		if (size > 0)
		{
			aBlock[15] = (byte) ((ctr) & 0xFF);
			aesEngine.processBlock(aBlock, 0, sBlock, 0);
			for (i = 0; i < size; i++)
			{
				encBuffer[bufferIndex + i] = (byte) (data[bufferIndex + i] ^ sBlock[i]);
			}
		}
	}

}