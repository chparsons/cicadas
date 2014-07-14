package chparsons.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.TreeSet;
import java.util.Set;

public class Utils 
{
	static public Properties load_props(String filename)
	{
		Properties props = create_sorted_props(); 
			
		FileInputStream fin = null;
		try 
		{
		    fin = new FileInputStream(filename);
		} 
		catch (FileNotFoundException e) 
		{
		    e.printStackTrace();
		    return null;
		}
		try 
		{
		    if (fin != null) 
		    {
			props.load(fin);
			fin.close();
		    }
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		    return null;
		}
		return props;
	}

	static public void save_props(Properties props, String filename)
	{
		save_props(props, filename, "christian parsons\nwww.chparsons.com.ar");
	}

	static public void save_props(Properties props, String filename, String comments)
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(filename);
			props.store(fout, comments);
			fout.close();	
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	static public Properties create_sorted_props()
	{
		return new Properties()
		{
			@Override
			public Set<Object> keySet()
			{
				return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
			}

			@Override
			public synchronized Enumeration<Object> keys() 
			{
				return Collections.enumeration(new TreeSet<Object>(super.keySet()));
				//Enumeration keysEnum = super.keys();
				//Vector<String> keyList = new Vector<String>();
				//while( keysEnum.hasMoreElements() )
					//keyList.add((String)keysEnum.nextElement());
				//Collections.sort(keyList);
				//return keyList.elements();
			}
		}; 
	}

	static public void call(Object obj, String method, Object[] args, Class[] argtypes)
	{
		try 
		{
			//Class[] argtypes = new Class[args.length];
			//for (int i = 0; i < args.length; i++)
			//{
				//argtypes[i] = args[i].getClass();
			//}
			java.lang.reflect.Method m;
			m = obj.getClass().getMethod(method, argtypes);
			try 
			{
				m.invoke(obj, args);
			} 
			catch (IllegalArgumentException e){} 
			catch (IllegalAccessException e){} 
			catch (java.lang.reflect.InvocationTargetException e){} 
		}
		catch (SecurityException e){} 
		catch (NoSuchMethodException e){}
	}
}
