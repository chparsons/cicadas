package cicada.events;

import controlP5.ControlEvent;

public class UIEvent
{
	private ControlEvent _e;

	public UIEvent(ControlEvent e)
	{
		this._e = e;
	}

	public ControlEvent event()
	{
		return _e;
	}

	public boolean is_widget(String type)
	{
		try { return _e.name().split("#")[0].equals(type); }
		catch (Error err) { return false; }
	}

	public void print(String prefix)
	{
		System.out.print(prefix+" ui event / name: "+_e.name()+", id: "+_e.id());
		if (_e.isController())
		{
			System.out.print(" /controller "+_e.controller());
		}
		System.out.println("");
	}
}
