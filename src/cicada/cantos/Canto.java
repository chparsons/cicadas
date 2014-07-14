package cicada.cantos;

import java.util.ArrayList;
import controlP5.ControlTimer;
import cicada.*;

public class Canto
{
	private int _id;
	private ArrayList<Keypoint> keys;

	private boolean _playing;
	private boolean _loop;

	private int _head, _length;
	private long tstart, telapsed, tprev, dt;

	public Canto(int id, ArrayList<Keypoint> keys)
	{
		this._id = id;
		this.keys = keys;

		_length = 0;
		for (Keypoint k : keys)
			_length += k.millis();

		reset_head();
		loop();
		stop();
	}

	public void dispose() 
	{
		keys.clear();
		keys = null;
	}

	public Canto clone()
	{
		return new Canto( _id, clone_keys() );
	}

	public ArrayList<Keypoint> get_keys()
	{
		//return keys;
		//return (ArrayList<Keypoint>)keys.clone();
		return clone_keys();
	}

	private ArrayList<Keypoint> clone_keys()
	{
		ArrayList<Keypoint> clone = new ArrayList<Keypoint>();
		for (int i = 0; i < keys.size(); i++)
		{
			clone.add( keys.get(i).clone() );
		}
		return clone;
	}

	public int frec()
	{
		if (!_playing) return 0;
		//no interpolation
		int kmillis = 0;
		for (Keypoint k : keys)
		{
			kmillis += k.millis();
			if (kmillis > _head)
				return k.frec();
		}
		return 0;
	}

	public boolean tick()
	{
		long t = Timer.time();
		dt = t - tprev;
		telapsed = t - tstart;
		tprev = t;
		if (!_playing) 
			return false;
		_head += dt;
		if (_head > _length)
		{	
			reset_head();
			return true;
		}
		return false;
	}
	
	private void play()
	{
		tstart = Timer.time();
		tprev = tstart;
		_playing = true;
		_loop = false;
	}

	public void loop()
	{
		play();
		_loop = true;
	}

	public void stop()
	{
		_playing = false;
		reset_head();
	}

	public void pause()
	{
		_playing = false;
	}

	private void reset_head()
	{
		_head = 0;
	}	

	public void sync_time(float t)
	{
		_head = (int)(_length * t);
	}

	public float time()
	{
		return (float)_head/_length;
	}

	public int length()
	{
		return _length;
	}

	public int id()
	{
		return _id;
	}

	public String toString()
	{
		return "[ "+_id+": "+keys.toString()+" ]";
	}
}

