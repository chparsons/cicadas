package chparsons.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.Random;

/**
 * @author Christian Parsons 
 * www.chparsons.com.ar 
 */

public class Mat 
{
	static public final float PI = (float)Math.PI;
	static public final float TWO_PI = PI * 2f;
	static public final float HALF_PI = PI * .5f;
	static public final float DEG_TO_RAD = PI / 180.0f;
	static public final float RAD_TO_DEG = 180.0f / PI;
    
	static private Random _rand;
    
	static public float lerp2d(float x, 
			float x1, float x2, float y1, float y2)
	{
		return (x-x1) / (x2-x1) * (y2-y1) + y1;
	}

	static public float lerp(float ini, float end, float factor)
	{
		if (ini == end) return ini;
		return ((1-factor) * ini) + (factor * end);
	}

	static public float rand()
	{
		check_rand();
		return _rand.nextFloat();
	}

	static public float rand(float max)
	{
		return rand() * max;
	}

	static public float rand(float min, float max)
	{
		if (min == max) return min;
		if (min > max) return rand(max, min);
		float diff = max - min;
		return rand(diff) + min;
	}

	static public float rand_around(float value, float range)
	{
		return rand(value-(float)(range*.5), value+(float)(range*.5));
	}

	static public int rand_int(int max)
	{
		if (max == 0) return max;
		check_rand();
		return _rand.nextInt(max);
	}

	static public int rand_int(int min, int max)
	{
		if (min == max) return min;
		if (min > max) return rand_int(max, min);
		int diff = max - min;
		return rand_int(diff) + min;
	}
	
	static public int rand_around(int value, int range)
	{
		return rand_int(value-(int)(range*.5), value+(int)(range*.5));
	}

	static public Object rand_prob(HashMap<?,Float> map)
	{
		Collection<Float> probs = map.values();
		float n = sum(probs) * rand();
		for (float p : probs)
		{
			n -= p;
			if (n < 0) 
			{
				List<?> objs = ArrayUtil.get_keys(map,p);
				int rnd = rand_int(0, objs.size());
				return objs.get(rnd);
			}
		}
		return null;
	}

	static public float sum(Collection<Float> values)
	{
		float s = 0;
		for (float n : values)
			s += n;
		return s;
	}

	static public int rand_sign()
	{
		return rand_sign(0.5f);
	}

	static public int rand_sign(float negativeProb)
	{
		return rand() < negativeProb ? -1 : 1;
	}

	static private void check_rand()
	{
		if (_rand == null) _rand = new Random();	
	}

	static public boolean isZero(float n, float TOL)
	{
		return n < TOL && n > -TOL;
	}

	static public boolean equals(float n1, float n2, float TOL)
	{
		return isZero(abs(n1 - n2), TOL);
	}

	static public float clamp(float input, float min, float max) 
	{
		return (input < min) ? min : (input > max) ? max : input;
	}

	static public float distanceSq(float x1, float y1, 
			float x2, float y2)
	{
		return distanceSq(x1, y1, 0, x2, y2, 0);	
	}

	static public float distanceSq(float x1, float y1, float z1, 
			float x2, float y2, float z2)
	{
		x1 -= x2;
		y1 -= y2;
		z1 -= z2;
		return (x1 * x1 + y1 * y1 + z1 * z1);
	}

	static public float abs(float n) 
	{
		if (n < 0) return -n;
		return n;
	}

	static public int abs(int n) 
	{
		if (n < 0) return -n;
		return n;
	}

	static public int floor(int n)
	{
		return (int)Math.floor( (double)n );
	}

	static public float floor(float n)
	{
		return (float)Math.floor( (double)n );
	}

	static public Map.Entry max(HashMap<?,Float> map)
	{
		float max = Float.MIN_VALUE;
		Map.Entry emax = null;
		Iterator it = map.entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry)it.next();
			Object k = entry.getKey();
			float n = (Float)entry.getValue();
			if (n > max) 
			{
				max = n;
				emax = entry;
			}
		}
		return emax;
	}

	static public Map.Entry min(HashMap<?,Float> map)
	{
		float min = Float.MAX_VALUE;
		Map.Entry emin = null;
		Iterator it = map.entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry)it.next();
			Object k = entry.getKey();
			float n = (Float)entry.getValue();
			if (n < min) 
			{
				min = n;
				emin = entry;
			}
		}
		return emin;
	}

	static public Map.Entry max_int(HashMap<?,Integer> map)
	{
		int max = Integer.MIN_VALUE;
		Map.Entry emax = null;
		Iterator it = map.entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry)it.next();
			Object k = entry.getKey();
			int n = (Integer)entry.getValue();
			if (n > max) 
			{
				max = n;
				emax = entry;
			}
		}
		return emax;
	}

	static public Map.Entry min_int(HashMap<?,Integer> map)
	{
		int min = Integer.MAX_VALUE;
		Map.Entry emin = null;
		Iterator it = map.entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry)it.next();
			Object k = entry.getKey();
			int n = (Integer)entry.getValue();
			if (n < min) 
			{
				min = n;
				emin = entry;
			}
		}
		return emin;
	}

	static public float max(Collection<Float> numlist)
	{
		float max = Float.MIN_VALUE;
		Iterator it = numlist.iterator();
		while (it.hasNext())
		{
			Float n = (Float)it.next();
			if (n > max) max = n;
		}
		return max;
	}

	static public float min(Collection<Float> numlist)
	{
		float min = Float.MAX_VALUE;
		Iterator it = numlist.iterator();
		while (it.hasNext())
		{
			Float n = (Float)it.next();
			if (n < min) min = n;
		}
		return min;
	}

	static public int max(Collection<Integer> numlist)
	{
		int max = Integer.MIN_VALUE;
		Iterator it = numlist.iterator();
		while (it.hasNext())
		{
			Integer n = (Integer)it.next();
			if (n > max) max = n;
		}
		return max;
	}

	static public int min(Collection<Integer> numlist)
	{
		int min = Integer.MAX_VALUE;
		Iterator it = numlist.iterator();
		while (it.hasNext())
		{
			Integer n = (Integer)it.next();
			if (n < min) min = n;
		}
		return min;
	}

	/**
	 * @param angle in radians
	 * @param radius
	 * @return float[0] x float[1] y
	 */
	static public float[] polar2cart(float angle, float radius)
	{
		return new float[] { 
				radius * (float)Math.cos(angle), 
				radius * (float)Math.sin(angle)		
		};
	}	

	/**
	 * @param x
	 * @param y
	 * @return float[0] angle float[1] radius
	 */
	static public float[] cart2polar(float x, float y)
	{
		return new float[] { 
				(float)Math.atan2(y, x), 
				(float)Math.sqrt(x*x + y*y)		
		};
	}
}
