package cicada.events;

import controlP5.*;
import chparsons.utils.*;

public class ControlEventProcessor
{
	public ControlEventProcessor() {}

	public void call_method(Object caller, Object _e)
	{
		if ( ! (_e instanceof ControlEvent) )
			return;
		ControlEvent e = (ControlEvent)_e;
		if ( ! e.isController() )
			return;
		_call( caller, e.controller().name(), e.controller() );	
	}

	private void _call(Object caller, String method, Controller ctrl)
	{
		Utils.call( caller, method, 
				new Controller[] { ctrl },
			 	new Class[] { Controller.class} );
	}
}
