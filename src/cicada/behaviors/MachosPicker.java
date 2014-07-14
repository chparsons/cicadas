package cicada.behaviors;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import chparsons.utils.*;
import cicada.*;
import cicada.config.*;
import cicada.cantos.*;

public class MachosPicker implements Configurable
{
	public String[] machos_filtros;
	private Tempo tempo;

	public MachosPicker() {}

	public Configurable set_config(Config config)
	{
		tempo = new Tempo(config.machos_tempo);

		this.machos_filtros = config.machos_filtros;	

		return this;
	}
	
	public Cicada update(Collection<Cicada> cicadas, Population population)
	{
		//Canto pop_model = population.get_canto_pop_model();
		//int pop_len = pop_model != null ? pop_model.length() : 0;
		//if ( tempo.tick( pop_len ) )
		if ( tempo.tick( 0 ) )
		{
			Cicada cicada = pick_by_space_distribution(cicadas);
			if (cicada == null) 
			{
				System.out.println("couldn't pick any macho");
				return null;
			}
			Canto canto = population.create_canto();
			cicada.make_macho( canto );
		}
		return null;
	}

	private Cicada pick_by_space_distribution(Collection<Cicada> cicadas)
	{
		if (cicadas.isEmpty()) 
		{
			System.out.println("couldn't pick any macho: there're no cicadas");
			return null;
		}	

		//check population in the 25% of nearest cicadas
		int cant_cicadas = cicadas.size();
		int cant_neighbors = (int)((float)cant_cicadas*.25);

		HashMap<Cicada,Float> probs = new HashMap<Cicada,Float>();
		Iterator it = cicadas.iterator();
		while ( it.hasNext() )
		{
			Cicada c = (Cicada)it.next();
			float prob = 0;
			if ( ! c.filter(machos_filtros) )
			{
				prob = population_of_notsingers(
						c, cicadas, 
						cant_neighbors,
						cant_cicadas);
						//c, cicadas, 400.f);
			}
			probs.put( c, prob );
		}
		//System.out.println(" \t cant neighbors "+cant_neighbors+" of "+cant_cicadas+", probs "+probs.size()+" >>> "+probs);
		
		//pick a cicada in an unpopulated place
		return (Cicada)Mat.rand_prob(probs);
	}

	private float population_of_notsingers(Cicada cicada, 
			Collection<Cicada> cicadas, 
			int cant_neighbors, int cant_cicadas)
	{
		//System.out.println("\t\t pop "+cicada);
		HashMap<Cicada,Float> neighbors = cicada.nearest_neighbors(
				cicadas, cant_neighbors, null); 
		//System.out.println("\t\t\t\t neightbors = "+neighbors);
		int pop = 0;
		for ( Cicada c : new ArrayList<Cicada>(neighbors.keySet()) )
		{
			if (!c.singing())
				pop++;
		}		
		//System.out.println("\t\t\t\t pop = "+pop);
		return (float)pop / cant_cicadas;
	}	

	public String toString()
	{
		return "[ machos picker "+tempo.toString()+" ]";
	}
}
