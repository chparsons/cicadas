package cicada;

import chparsons.utils.Mat;

public class Tempo
{
	private int trange[];
	private int tempo;
	private long tstamp, elapsed;

	public Tempo(int[] trange)
	{
		set_range(trange);
		reset(0);
	}

	public void set_range(int[] trange)
	{
		this.trange = trange;
	}

	public boolean tick(int snapto)
	{
		elapsed = Timer.time() - tstamp;
		if (tempo == 0 || elapsed < tempo)
			return false;
		reset(snapto);
		return true;
	}

	public float time()
	{
		if (tempo == 0) return 0;
		return (float)elapsed / tempo;
	}

	public void reset(int snapto)
	{
		tempo = Mat.rand_int(trange[0], trange[1]);
		tempo = snap(tempo, snapto);
		tstamp = Timer.time();
	}

	private int snap(int t, int snapto)
	{
		if (snapto == 0 || snapto > t)
			return t;
		snapto = Mat.abs(snapto);
		int unit =  (int)Mat.floor( (float)t / snapto );
		return unit * snapto;
	}

	public String toString() 
	{
		return "[ tempo: "+tempo+", elapsed: "+elapsed+", tmin: "+trange[0]+", tmax: "+trange[1]+" ]";
	}
}
