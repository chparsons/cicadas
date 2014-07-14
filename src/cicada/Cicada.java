package cicada;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import processing.core.PApplet;
import processing.core.PVector;
import chparsons.utils.*;
import chparsons.events.*;
import cicada.*;
import cicada.config.*;
import cicada.events.*;
import cicada.cantos.*;
import cicada.behaviors.*;

public class Cicada extends EventDispatcher implements EventListener
{
	private String _id;
	private PVector _loc;

	private boolean macho;
	private Cicada parent;

	private Population population;

	private Copiadora copiadora;
	private Sincronizador sync;

	private Canto canto;
	private Tempo _age;
	private boolean _aged;
	private Tempo _copia;

	public Cicada(String id, PVector loc, 
			Population population, Config config)
	{
		//System.out.println("new Cicada "+_id);
		this._id = id;
		this._loc = loc;

		this.population = population;

		//behaviors
		copiadora = new Copiadora();
		copiadora.set_config(config);

		sync = new Sincronizador();
		sync.set_config(config);

		macho = false;
		canto = null;

		_aged = false;
		_age = new Tempo( config.duracion_canto );
		_copia = new Tempo( config.copia_tempo );
	}

	public void dispose()
	{
		//System.out.println(this+" dispose");
		silence(); 
		copiadora = null;
		sync = null;
		population = null;
		dispose_parent();	
	}

	private void dispose_parent()
	{
		//System.out.println(this+" dispose parent "+parent);
		if (parent == null)
			return;
		parent.remove_listener(this);
		parent = null;
	}

	public void update(Collection<Cicada> cicadas)
	{
		if ( !singing() ) 
		{
			dispatch( new CicadaEvent(CicadaEvent.UPDATE,this) ); 
			return;
		}
			
		if ( canto.tick() )
		{
			//sync to its model
			set_canto( population.get_model( canto ) );
		}

		if ( macho && _aged ) 
		{
			int pop = population.get_pop_qty(canto);
			if ( pop == 1 )
				silence();
			dispatch( new CicadaEvent(CicadaEvent.UPDATE,this) ); 
			return;
		}

		//System.out.println(this+" copia "+_copia.time());
		if ( _copia.tick( 0 ) )
		{
			propagate( copiadora.pick(this,cicadas,population) );
		}

		_aged = _age.tick( canto.length() ); 

		if ( _aged && !macho )
		{
			silence();	
		}

		dispatch( new CicadaEvent(CicadaEvent.UPDATE,this) );
	}	

	private void propagate(Collection<Cicada> vecinas)
	{
		//if (vecinas.isEmpty())
			//System.out.println("\t"+this+" no vecinas to propagante!");
		for (Cicada v : vecinas)
		{
			//v.copy( sync.process( this ) );
			v.copy( this );
		}
	}	

	private void copy(Cicada parent)
	{
		dispose_parent();
		this.parent = parent;
		parent.add_listener(this);

		sing( population.get_model( parent.canto ) );
	}	

	public void on_event(Object _e)
	{
		process_parent_event(_e);
	}

	private void process_parent_event(Object _e)
	{
		if ( !(_e instanceof CicadaEvent) )
			return;
		CicadaEvent e = (CicadaEvent)_e;	

		if ( e.type().equals(CicadaEvent.CANTO_STOP)
			&& e.cicada().equals(parent) )
		{
			dispose_parent();	
		}
	}

	private void sing(Canto _canto)
	{
		if ( !singing() ) 
		{
			_aged = false;
			_age.reset( _canto.length() );
			_copia.reset( 0 );
		}
		set_canto( _canto );
		dispatch( new CicadaEvent(CicadaEvent.CANTO_START,this) );
	}

	private void set_canto(Canto _canto)
	{
		if ( singing() ) 
			canto.dispose();

		//canto = _canto;
		//canto.loop();

		//sync
		canto = sync.process( _canto );
		
		//phase to its current macho
		Cicada macho = population.get_macho( canto );
		if (macho != null)
			sync.phase( canto, macho.canto_time() );

		//start
		canto.loop();
	}

	public void make_macho(Canto canto)
	{
		//System.out.println("macho added @cicada"+_id+",canto:"+canto);
		Cicada curr_macho = population.get_macho(canto);
		if (curr_macho != null)
			curr_macho.silence();

		macho = true;
		sing( canto );
		dispatch( new CicadaEvent(CicadaEvent.MACHO_ADDED,this) );
	}

	public void silence()
	{
		dispatch(new CicadaEvent(CicadaEvent.CANTO_STOP,this)); 

		if ( singing() ) 
			canto.dispose();
		canto = null;

		if (macho)
		{
			macho = false;
			dispatch(new CicadaEvent(CicadaEvent.MACHO_REMOVED,this)); 
		}
		
		_aged = false;
		_age.reset(0);
		_copia.reset(0);
	}	

	static public final String FILTER_MACHOS = "is_macho";
	static public final String FILTER_SINGERS = "is_singing";

	public boolean filter(String[] filters)
	{
		if (filters == null) 
			return false;
		for (String f : filters)
			if ( apply_filter(f) )
				return true;
		return false;
	}

	private boolean apply_filter(String filter)
	{
		if (filter.equals( FILTER_MACHOS ))
		{
			return is_macho();
		}
		else if (filter.equals( FILTER_SINGERS ))
		{
			return singing();
		}
		//System.out.println("Cicada filter "+filter+" not supported");
		return true;
	}

	public HashMap<Cicada,Float> nearest_neighbors(
			Collection<Cicada> cicadas, int max, String[] filtros)
	{
		HashMap<Cicada,Float> nearest = new HashMap<Cicada,Float>();
		//ArrayList<Cicada> nearest = new ArrayList<Cicada>();
		//ArrayList<Float> nearest_d = new ArrayList<Float>();

		//init nearest map
		Iterator it = cicadas.iterator();
		while ( nearest.size() < max && it.hasNext() )
		{
			Cicada c = (Cicada)it.next();
			if ( c.equals( this ) || c.filter(filtros) ) 
				continue;
			float d = this.dist( c );
			nearest.put( c, d );
			//nearest_d.add(d);
			//nearest.add(c);
		}
		//System.out.println("\t\t\t\t\t\t -- nearest "+nearest.size());
		//insert max nearest neighbors
		it = cicadas.iterator();
		while ( it.hasNext() )
		{
			Cicada c = (Cicada)it.next();
			if ( c.equals( this ) || c.filter(filtros) 
				|| nearest.containsKey(c) )
				continue;
			//System.out.println("\t\t\t\t\t\t -- nearest "+nearest);
			//System.out.println("\t\t\t\t\t\t -- nearest "+nearest_d);
			//float dmax = Mat.max(nearest_d);
			Map.Entry _emax = Mat.max(nearest);
			float dmax = (Float)_emax.getValue();
			float d = this.dist( c );
			if (d < dmax)
			{
				//int imax = nearest_d.indexOf(dmax);
				//nearest_d.set(imax, d);
				//nearest.set(imax, c);
				Cicada cmax = (Cicada)_emax.getKey();
				nearest.remove( cmax );
				nearest.put( c, d );
			}
		}
		//System.out.println("\t\t\t\t\t\t -- nearest size "+nearest.size());
		return nearest;
		//HashMap<Cicada,Float> hash = new HashMap<Cicada,Float>();
		//for (int i = 0; i < nearest.size(); i++)
		//{
			//hash.put( nearest.get(i), nearest_d.get(i) );
		//}
		//System.out.println("\t\t\t\t\t\t -- hash size "+hash.size());
		//return hash;
	}

	public Cicada get_parent()
	{
		return parent;
	}

	public boolean is_macho()
	{
		return macho;
	}

	public boolean singing()
	{
		return canto != null;
	}	

	public int frec()
	{
		return singing() ? canto.frec() : 0;
	}

	public float dist(Cicada other)
	{
		return _loc.dist( other._loc );
	}

	public float age()
	{
		return _age.time();
	}

	public PVector loc()
	{
		return new PVector(_loc.x, _loc.y, _loc.z);
	}

	public String id()
	{
		return _id;
	}

	//public Canto get_canto()
	//{
		//return singing() ? canto.clone() : null;
	//}

	public float canto_time() 
	{
		return singing() ? canto.time() : 0;
	}

	public int canto_id()
	{
		return singing() ? canto.id() : -1;
	}

	public boolean equals(Cicada other)
	{
		//string comp
		return this.id().equals( other.id() );
	}

	public String toString()
	{
		//return "[cicada "+_id+", canto "+canto_id()+", macho "+is_macho()+"]";
		return "["+_id+"]";
	}
}
