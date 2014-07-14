package cicada.cantos;

import chparsons.utils.Mat;

public class Keypoint
{
	static public final int MAX_FREC = 255;

	private int _frec, _millis;
	
	public Keypoint(int frec, int millis)
	{
		_frec = (int)Mat.clamp( frec, 0, MAX_FREC );
		_millis = (int)Mat.clamp( millis, 0, (float)Integer.MAX_VALUE );
	}

	public int millis() { return _millis; };
	public int frec() { return _frec; };

	public Keypoint clone()
	{
		return new Keypoint(_frec, _millis);
	}

	public String toString()
	{
		return "[f:"+_frec+",m:"+_millis+"]";
	}
}

