package test2;

import java.util.Arrays;

import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Base64;

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
		byte[] decode = Base64.decode(base64encodedData);
		StringBuilder hexaRes = new StringBuilder();
		String[] message = new String[decode.length];
		int i = 0;

		for (byte b : decode)
		{
			hexaRes.append(String.format("%02X ", b));
			message[i] = String.format("%02X", b);
			i++;
		}
		System.out.print("Message re�u (encod�) : ");
		for (i = 0; i < message.length; i++)
			System.out.print(message[i] + " ");
		System.out.println();

		int sequence = Integer.parseInt(message[7] + message[6], 16);

		byte[] addrbytes = Arrays.copyOfRange(decode, 1, 5);

		String addressReverse = TestDecrypter.convertToHexa(addrbytes);

		System.out.println("Adresse du device : " + getAddressDevice(addrbytes) + " \nnumeroSequence : " + sequence);

		int address = java.nio.ByteBuffer.wrap(addrbytes).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
		// Move to offset of data

		byte[] payload = new byte[decode.length - 9];
		System.arraycopy(decode, 9, payload, 0, decode.length - 9);

		byte[] decrypted = new byte[payload.length];

		return decrypthelper(payload, 16, address, 0, 0x95, decrypted);
	}

	private byte[] decrypthelper(byte[] data, int len, int address, int dir, int sequenceCounter, byte[] encBuffer)
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
		return encBuffer;
	}
}
