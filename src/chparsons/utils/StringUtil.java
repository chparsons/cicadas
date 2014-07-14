package chparsons.utils;

public class StringUtil
{
	static public boolean is_empty(String str)
	{
		return str.replaceAll(" ", "").isEmpty();
	}
}
