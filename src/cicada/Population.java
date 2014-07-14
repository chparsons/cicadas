package cicada;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import chparsons.utils.*;
import chparsons.events.*;
import cicada.Cicada;
import cicada.cantos.*;
import cicada.config.*;
import cicada.events.*;

public class Population implements Configurable, EventListener
{
	private ArrayList<String> cantos;
	private ArrayList<String> normales;
	private ArrayList<String> alertas;
	private float[] mass_coef;

	private HashMap<Integer,Canto> models; //by canto id
	private HashMap<Integer,Cicada> machos; //by canto id
	private HashMap<Integer,Integer> cantos_population;

	private Map.Entry _canto_pop_data;
	private Canto _canto_pop_model;
	
	private int _cant_singers = 0;

	public Population() {}

	public Configurable set_config(Config config)
	{
		//e.g. canto_x=11,111/22,222/33,333/44,444/55,555
		this.cantos = config.cantos;
		this.normales = config.normales;
		this.alertas = config.alertas;

		this.mass_coef = config.mass_coef;	

		cantos_population = new HashMap<Integer,Integer>();
		machos = new HashMap<Integer,Cicada>();
		models = new HashMap<Integer,Canto>();

		for (int i = 0; i < cantos.size(); i++)
		{
			cantos_population.put(i, 0);
			Canto model = _create_canto( i, cantos.get(i) );
			models.put( i, model );
		}

		//System.out.println("Population \n"+
				//"\t cantos "+cantos+"\n"+
				//"\t alertas "+alertas+"\n"+
				//"\t cantos_population "+cantos_population+"\n");

		return this;
	}

	public int get_pop_qty(Canto canto)
	{
		return cantos_population.get( canto.id() );
	}

	public int canto_pop_id()
	{
		return _canto_pop_data != null ?
			(Integer)_canto_pop_data.getKey() : -1; 
	}

	public int canto_pop_qty()
	{
		return _canto_pop_data != null ?
			(Integer)_canto_pop_data.getValue() : 0;
	}

	public boolean is_pop(Cicada c)
	{
		return canto_pop_id() == c.canto_id(); 
	}

	public Cicada get_macho(Canto canto)
	{
		return machos.get( canto.id() );
		//Cicada m = machos.get( canto.id() );
		//return m != null ? m.canto_time() : -1;
	}	

	public Canto get_model(Canto canto)
	{
		return models.get( canto.id() );
	}

	public Canto get_canto_pop_model()
	{
		return _canto_pop_model; //clone?
	}

	public int cant_singers()
	{
		return _cant_singers;
	}	

	public Canto create_canto()
	{
		int id = pick_canto_id_by_pop();
		String canto_str = cantos.get( id );
		return _create_canto( id, canto_str );
	}

	private int pick_canto_id_by_pop()
	{
		HashMap<Integer,Float> probs = new HashMap<Integer,Float>();
		//just for debugging
		HashMap<Integer,Float> pops = new HashMap<Integer,Float>();

		double e = ((double)mass_coef[0] - 0.4d) * 2d;

		float min_pop = Mat.min( cantos_population.values() );
		min_pop = min_pop > 0 ? min_pop : 1f;
		float csingers = _cant_singers > 0 ? (float)_cant_singers : 1f;

		//System.out.println("pick canto pop, max_pop: "+max_pop);
		Iterator it = cantos_population.entrySet().iterator();
		while ( it.hasNext() )
		{
			Map.Entry entry = (Map.Entry)it.next();
			int _id = (Integer)entry.getKey();
			
			//TODO optimize list filter
			String canto_str = cantos.get( _id );
			if ( 	(Engine.state().equals(StateEvent.NORMAL)
				&& alertas.contains( canto_str ) )
				||
				(Engine.state().equals(StateEvent.ALERTA)
				&& normales.contains( canto_str ) )
			)
				continue;

			float _pop = (float)((Integer)entry.getValue());
			float pop = (_pop > 0 ? _pop : min_pop) / csingers;
			float prob = (float)Math.pow( (double)pop, e );
			probs.put( _id, prob );
			pops.put( _id, pop );
			//System.out.println("\t "+_id+
					//" _pop: "+_pop+
					//" popf: "+popf+
					//" prob: "+prob);
		}

		int id = (Integer)Mat.rand_prob(probs);
		//System.out.println("pick macho"+
				//", selected id: "+id+
				//"\n\t probs: "+probs+
				//"\n\t pops: "+pops);
		return id;
	}

	private Canto _create_canto(int _id, String _canto_str)
	{
		ArrayList<Keypoint> keys = new ArrayList<Keypoint>();
		String[] klist = _canto_str.split(Sep.CANTO_KEYPOINTS);
		for (String kstr : klist)
		{
			String[] fm = kstr.split(Sep.KEYPOINT_FREC_MILLIS);
			int frec = Integer.parseInt( fm[0] );
			int millis = Integer.parseInt( fm[1] );
			keys.add( new Keypoint(frec, millis) );
		}
		return new Canto(_id, keys);
	}


	// events
	

	public void on_event(Object _e)
	{
		process_cicada_event(_e);
	}

	private void process_cicada_event(Object _e)
	{
		if ( !(_e instanceof CicadaEvent) )
			return;

		CicadaEvent e = (CicadaEvent)_e;	
		Cicada cicada = e.cicada();

		if ( e.type().equals(CicadaEvent.CANTO_START) )
		{
			canto_added( cicada.canto_id() );
		}
		else if ( e.type().equals(CicadaEvent.CANTO_STOP) 
				&& e.cicada().singing() )
		{
			canto_removed( cicada.canto_id() );
		}
		else if ( e.type().equals(CicadaEvent.MACHO_ADDED) ) 
		{
			machos.put( cicada.canto_id(), cicada );
		}
		else if ( e.type().equals(CicadaEvent.MACHO_REMOVED) ) 
		{
			machos.put( cicada.canto_id(), null );
		}
	}

	private void canto_added(int canto_id)
	{
		cantos_changed( 1, canto_id);	
	}

	private void canto_removed(int canto_id)
	{
		cantos_changed( -1, canto_id);	
	}

	private void cantos_changed(int amt, int canto_id)
	{
		Integer curr_pop = cantos_population.get(canto_id);
		if (curr_pop == null) curr_pop = 0;
		int pop = curr_pop + amt;

		cantos_population.put( canto_id, pop );

		//there are no more cicadas of this type
		//silence its macho if one exists..
		//if (pop == 1)
		//{
			//Cicada macho = machos.get( canto_id );
			//if (macho != null)
			//{
				//macho.silence();
			//}
		//}

		//stat canto popular, cantos with only 1 cicada are not pop...
		_canto_pop_data = Mat.max_int( cantos_population );
		if (canto_pop_qty() == 1) _canto_pop_data = null;

		int id = canto_pop_id(); 		
		if ( _canto_pop_model == null || id != _canto_pop_model.id() )
		{
			//String canto_str = cantos.get( id );
			//_canto_pop_model = _create_canto( id, canto_str );
			_canto_pop_model = models.get( id );
		}
		
		_cant_singers += amt;

		//System.out.println("Population on changed canto "+canto_id+", pop "+pop+", map: "+cantos_population);
	}

	//private ArrayList<Canto> parse_cantos(ArrayList<String> _cantos)
	//{
		//ArrayList<Canto> cantos = new ArrayList<Canto>();
		//for (String cstr : _cantos)
			//cantos.add( create_canto(cstr) );
		//return cantos;
	//}
}
