package cicada;

public class Timer
{
	static public long time() { return t; };
	static private long t;
	static private long dt, tprev, tspeed, tspeedsaved;

	static public void init()
	{
		set_speed(1);
		tprev = System.currentTimeMillis();
	}

	static public boolean update()
	{
		long curr = System.currentTimeMillis();
		dt = (curr - tprev) * tspeed;
		tprev = curr;
		t += dt;
		return dt > 0;
	}

	static public void play() 
	{	
		tspeed = tspeedsaved;
	}

	static public void pause()
	{
		tspeed = 0;
	}

	static public boolean paused()
	{
		return tspeed == 0;
	}

	static public void toggle()
	{
		if (paused()) play();
		else pause();
	}

	static public void set_speed(long s)
	{
		tspeed = tspeedsaved = s;
	}
}
