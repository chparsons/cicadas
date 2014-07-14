package chparsons.utils;

import java.util.Properties;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseConfig
{
	private Properties props;
	private String file;

	public BaseConfig(String file)
	{
		this.file = file;
		props = Utils.load_props(file);
	}

	public void set_prop(String key, String value)
	{
		props.setProperty(key, value);
	}

	public String get_prop(String key)
	{
		return props.getProperty(key);
	}

	public String get_prop(String key, String defaultValue)
	{
		return props.getProperty(key, defaultValue);
	}

	public void save()
	{
		Utils.save_props(props, file);
	}

	public void save(String comments)
	{
		Utils.save_props(props, file, comments);
	}

	public HashMap<String,String> get_map(String prop_key, String sep)
	{
		HashMap<String,String> map = new HashMap<String,String>();
		Enumeration em = props.keys();
		while (em.hasMoreElements()) 
		{
			String str = (String)em.nextElement();
			String[] prop_name = str.split(sep);
			if (prop_name[0].equals(prop_key))
				//System.out.println(str+":"+props.get(str));
				map.put( prop_name[1], (String)props.get(str) );
		}
		return map;
	}

	public ArrayList<String> get_list(String prop_key, String sep)
	{
		ArrayList<String> list = new ArrayList<String>();
		Enumeration em = props.keys();
		while (em.hasMoreElements()) 
		{
			String str = (String)em.nextElement();
			if (str.split(sep)[0].equals(prop_key))
				//System.out.println(str+":"+props.get(str));
				list.add( (String)props.get(str) );
		}
		return list;
	}

	public String[] get_list_str(String prop_key, String sep)
	{
		String prop = props.getProperty(prop_key);
		if ( StringUtil.is_empty(prop) ) return new String[0];
		return prop.split(sep);
	}

	public int[] get_range_int(String prop_key, String sep)
	{
		String[] r = props.getProperty(prop_key).split(sep);
		int[] range = {
			Integer.parseInt( r[0] ),
			Integer.parseInt( r[1] )
		};
		return range;
	}

	// ints
	
	protected int get_int(String prop_key)
	{
		return Integer.parseInt( props.getProperty(prop_key) ); 
	}
	
	protected int[] get_int_ptr(String prop_key)
	{
		return new int[] { get_int(prop_key) };
	}

	protected String int_range_to_str(int[] range, String sep)
	{
		return range[0]+sep+range[1];
	}	

	protected String int_ptr_to_str(int[] ptr)
	{
		return String.valueOf( ptr[0] );
	}	

	// floats

	protected float get_float(String prop_key)
	{
		return Float.parseFloat( props.getProperty(prop_key) ); 
	}

	protected float[] get_float_ptr(String prop_key)
	{
		return new float[] { get_float(prop_key) };
	}	

	protected String float_ptr_to_str(float[] ptr)
	{
		return String.valueOf( ptr[0] );
	}
}
