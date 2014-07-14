package cicada;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import processing.core.PVector;
import controlP5.*;
import chparsons.events.*;
import chparsons.utils.*;
import cicada.events.*;
import cicada.cantos.*;
import cicada.behaviors.*;
import cicada.ui.*;
import cicada.config.*;

public class Engine extends EventDispatcher implements EventListener
{
	private Config config;
	private ControlEventProcessor ctrl_event_processor;

	private ControlTimer timer;

	private HashMap<String,Cicada> cicadas;
	//private ArrayList<Cicada> singers;

	private MachosPicker machos_picker;
	private Population population;

	//last @#$%ˆ&* minute deadline code.. :)
	static private String _state = StateEvent.NORMAL;
	static public String state() { return _state; };

	public Engine(Config config)
	{
		this.config = config;

		timer = new ControlTimer();

		cicadas = new HashMap<String,Cicada>();
		//singers = new ArrayList<Cicada>();

		ctrl_event_processor = new ControlEventProcessor();

		population = new Population();
		population.set_config(config);

		machos_picker = new MachosPicker();
		machos_picker.set_config(config);
	}

	public void init()
	{
	}

	public void update()
	{
		//singers.clear();
		Collection<Cicada> _cicadas = cicadas.values();
		
		update_cicadas(_cicadas);
		machos_picker.update(_cicadas, population);	
	}

	private void update_cicadas(Collection<Cicada> _cicadas)
	{
		Iterator it = _cicadas.iterator();
		while ( it.hasNext() )
		{
			Cicada c = (Cicada)it.next();
			c.update(_cicadas);
			//if (c.singing())
				//singers.add(c);
		}
	}

	//private void update_cicadas(HashMap<Integer,Cicada> _cicadas)
	//{
		//Set cicset = _cicadas.entrySet();
		//Iterator it = cicset.iterator();
		//while (it.hasNext())
		//{
			//Map.Entry m = (Map.Entry)it.next();
			//Cicada c = (Cicada)m.getValue();
			//c.update( new ArrayList<Cicada>(cicset) );
			//if (c.singing())
				//singers.add(c);
		//}
	//}
	
	public void silence_all_cicadas(Controller ctrl)
	{
		Collection<Cicada> _cicadas = cicadas.values();
		for (Cicada c : _cicadas)
		{
			c.silence();
		}
	}

	private void add_cicada(Cicada c)
	{
		c.add_listener(this);
		c.add_listener(population);
		cicadas.put( c.id(), c);
	}

	private void dispose_cicada(Cicada c)
	{
		c.dispose(); //let dispatch dispose event
		c.remove_listener(this);
		c.remove_listener(population);
		cicadas.remove( c.id() );
	}

	private void create_cicada(String id, PVector loc)
	{
		//System.out.println("engine add cicada "+id);
		if (cicadas.containsKey(id))
		{
			System.out.println("couldn't add cicada "+id+" because there already exists one");
			return;
		}

		Cicada c = new Cicada(id, loc, population, config);
		add_cicada(c);	
	}

	//public ArrayList<Cicada> get_singers()
	//{
		//return singers;
	//}

	public Collection<Cicada> get_cicadas()
	{
		return cicadas.values();
	}


	// events
	

	public void on_event(Object _e)
	{
		//System.out.println("Engine listens to "+_e);
		process_state_event(_e);
		process_engine_event(_e);
		process_cicada_event(_e);
		process_ui_event(_e);
		ctrl_event_processor.call_method(this, _e);
	}

	private void process_state_event(Object _e)
	{
		if ( !(_e instanceof StateEvent) )
			return;

		StateEvent e = (StateEvent)_e;
		System.out.println("engine received state event ("+e.type()+")");

		if ( e.type().equals(StateEvent.START) )
		{
			//System.out.println("\t start! ");
			play(null);
		}
		else if ( e.type().equals(StateEvent.STOP) )
		{
			//System.out.println("\t stop! ");
			stop(null);
		}
		else if ( e.type().equals(StateEvent.SILENCE) )
		{
			//System.out.println("\t silence! ");
			silence_all_cicadas(null);
		}
		else if ( e.type().equals(StateEvent.ALERTA) )
		{
			//System.out.println("\t alerta! ");
			_state = StateEvent.ALERTA;
		}
		else if ( e.type().equals(StateEvent.NORMAL) )
		{
			System.out.println("\t normal! ");
			_state = StateEvent.NORMAL;
		}
	}

	private void process_engine_event(Object _e)
	{
		if ( !(_e instanceof EngineEvent) )
			return;

		EngineEvent e = (EngineEvent)_e;
		if ( e.type().equals(EngineEvent.SILENCE_ALL) )
		{
			silence_all_cicadas(null);
			return;
		}
	}

	private void process_cicada_event(Object _e)
	{
		if ( !(_e instanceof CicadaEvent) )
			return;

		//System.out.println("Engine listens to "+((CicadaEvent)_e));
		dispatch(_e);	
	}

	private void process_ui_event(Object _e)
	{
		if ( !(_e instanceof ControlEvent) )
			return;
		//System.out.println("Engine listens to "+((ControlEvent)_e));

		process_toggle_event((ControlEvent)_e);
	}

	private void process_toggle_event(ControlEvent e)
	{
		if ( !e.isController() || !(e.controller() instanceof Toggle) )
			return;
		//System.out.println("Engine listens to "+e.controller());

		String cic_id = get_cicada_id(e);
		if ( StringUtil.is_empty(cic_id) ) 
			return;

		Toggle t = (Toggle)e.controller();
		CVector3f tp = t.position();
		//CVector3f tp = t.absolutePosition();
		PVector loc = new PVector(tp.x,tp.y,tp.z);
		if (e.value() == 1) 
		{
			create_cicada( cic_id, loc );
		}
		else
		{
			Cicada c = (Cicada)cicadas.get( cic_id );
			//if (!c.singing())
				dispose_cicada(c);
			//else
				//t.toggle(); 
		}
	}	

	private String get_cicada_id(ControlEvent e)
	{
		//return e.event().label();
		return e.label();
		//String label = e.label();
		//if (label == "") return -1;
		//return Integer.parseInt(label);
	}	

	public void play(Controller ctrl) 
	{ 
		Timer.play(); 
	}

	public void stop(Controller ctrl) 
	{ 
		silence_all_cicadas(null);
		Timer.pause();
	}

	public String get_time_str()
	{
		return timer.toString()+"\t\t t = "+Timer.time()+" "+machos_picker.toString();
	}
}
