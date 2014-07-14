package chparsons.events;

import javax.swing.event.EventListenerList;

public class EventDispatcher
{
	protected EventListenerList listener_list = new EventListenerList();

	public void add_listener(EventListener listener) 
	{
		listener_list.add(EventListener.class, listener);
	}

	public void remove_listener(EventListener listener) 
	{
		listener_list.remove(EventListener.class, listener);
	}

	public void dispatch(Object e) 
	{
		Object[] listeners = listener_list.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) 
		{
			if (listeners[i] == EventListener.class) 
			{
				((EventListener)listeners[i+1]).on_event(e);
			}
		}
	}

	public void print_listeners() 
	{
		System.out.print("\t"+this+" listeners: ");
		Object[] listeners = listener_list.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) 
		{
			System.out.print("["+listeners[i+1]+" "+listeners[i]+"], ");
		}
		System.out.println();
	}
}

