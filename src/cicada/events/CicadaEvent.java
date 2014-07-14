package cicada.events;

import controlP5.ControlEvent;
import cicada.Cicada;

public class CicadaEvent
{
	static public final String UPDATE = "cicada_update";
	static public final String MACHO_ADDED = "macho_added";
	static public final String MACHO_REMOVED = "macho_removed";
	static public final String CANTO_START = "canto_start";
	static public final String CANTO_STOP = "canto_stop";

	private String _type;
	private Cicada _c;

	public CicadaEvent(String type, Cicada c)
	{
		_type = type;
		_c = c;
	}

	public String type()
	{
		return _type;
	}

	public Cicada cicada()
	{
		return _c;
	}

	public String toString()
	{
		return "[CicadaEvent, type: "+_type+", cicada: "+_c.id()+"]";
	}	
}
