package chparsons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

public class ArrayUtil
{
	public static List<?> get_values(Map<?,?> map, Object key)
	{
		ArrayList<Object> values = new ArrayList<Object>();
		if ( !map.containsKey(key) ) 
			return values;
		Iterator it = map.entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry)it.next();
			Object k = entry.getKey();
			Object v = entry.getValue();
			if ( key.equals(k) ) values.add(v);
		}
		return values;
	}

	public static List<?> get_keys(Map<?,?> map, Object value)
	{
		ArrayList<Object> keys = new ArrayList<Object>();
		if ( !map.containsValue(value) ) 
			return keys;
		for ( Object k : map.keySet() )
		{
			Object v = map.get(k);
			if ( value.equals(v) ) keys.add(k);
		}
		return keys;
	}

	public static Object get_key(Map<?,?> map, Object value)
	{
		if ( !map.containsValue(value) ) 
			return null;
		for ( Object k : map.keySet() )
		{
			Object v = map.get(k);
			if ( value.equals(v) ) return k;
		}
		return null;
	}
}

