package test2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

/** This class allows to get the real information with
 * 
 * @author Ludovic */
public class LoraMoteMessage
{
	private byte[] payload;

	public LoraMoteMessage(byte[] payload)
	{
		this.payload = payload;
	}

	/** @return true or false */
	public boolean getLedState()
	{
		if (payload[0] == 0)
			return false;
		return true;
	}

	/** Temperature en hpa
	 * 
	 * @return */
	public float getAtmosphericPressure()
	{
		ByteBuffer byteBuffer = ByteBuffer.allocate(2);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.put(payload[1]);
		byteBuffer.put(payload[2]);
		return (float) byteBuffer.getShort(0) / 10;
	}

	public float getTemperature()
	{
		ByteBuffer byteBuffer = ByteBuffer.allocate(2);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.put(payload[3]);
		byteBuffer.put(payload[4]);
		return (float) byteBuffer.getShort(0) / 100;
	}

	public float getLatitude()
	{
		byte[] latArray = { payload[8], payload[9], payload[10] };
		String data = ByteUtils.toBinaryString(latArray);
		String[] dataBytes = data.split(" ");
		String res = "";
		for (int i = 0; i < dataBytes.length; i++)
			res += new StringBuilder(dataBytes[i]).reverse().toString();
		float latitude = 0;
		if (res.charAt(0) == '0')
		{
			int resusul = Integer.parseInt(res, 2);
			latitude = ((float) (90 * resusul) / ((float) 8388608));
		}
		else
		{
			int resusul = Integer.parseInt(res.substring(1), 2);
			latitude = -((float) (90 * resusul) / ((float) 8388608));
		}
		return latitude;
	}

	public float getLongitude()
	{
		byte[] latArray = { payload[11], payload[12], payload[13] };
		String data = ByteUtils.toBinaryString(latArray);
		String[] dataBytes = data.split(" ");
		String res = "";
		for (int i = 0; i < dataBytes.length; i++)
			res += new StringBuilder(dataBytes[i]).reverse().toString();
		float latitude = 0;
		if (res.charAt(0) == '0')
		{
			int resusul = Integer.parseInt(res, 2);
			latitude = ((float) (180 * resusul) / ((float) 8388608));
		}
		else
		{
			int resusul = Integer.parseInt(res.substring(1), 2);
			latitude = -((float) (180 * resusul) / ((float) 8388608));
		}
		return latitude;
	}

	public int getAltitude()
	{
		ByteBuffer byteBuffer = ByteBuffer.allocate(2);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		byteBuffer.put(payload[5]);
		byteBuffer.put(payload[6]);
		return (int) byteBuffer.getShort(0);
	}

	public static void main(String[] args)
	{

	}
}
