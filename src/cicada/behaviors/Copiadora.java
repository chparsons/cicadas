package cicada.behaviors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import processing.core.PVector;
import chparsons.utils.Mat;
import cicada.*;
import cicada.config.*;

public class Copiadora implements Configurable
{
	private int[] cant_vecinas; 
	private int[] radio_range; 
	private String[] copia_filtros;
	private float[] mass_coef;
	private float[] sinc_speed;

	public Copiadora() {}

	public Configurable set_config(Config config)
	{
		this.cant_vecinas = config.copia_cant_vecinas;
		this.radio_range = config.copia_radio;
		this.copia_filtros = config.copia_filtros;
		this.mass_coef = config.mass_coef;
		this.sinc_speed = config.sinc_speed;

		return this;
	}

	public Collection<Cicada> pick(Cicada cicada, 
					Collection<Cicada> cicadas,
					Population population)
	{
		ArrayList<Cicada> vecinas = new ArrayList<Cicada>();

		if ( cutoff( cicada, cicadas, population ) )
			return vecinas;

		// pick by nearest neighbor
		// elegir cant (cant_vecinas) entre las cant_nearest 
		// mas cercanas dentro del radio

		int radio, cant, cant_nearest;

		radio = Mat.rand_int(radio_range[0], radio_range[1]);
		
		if (cant_vecinas[0] == cant_vecinas[1])
		{
			cant = cant_vecinas[0];
			cant_nearest = cant * 4;
		}
		else
		{
			cant = Mat.rand_int(cant_vecinas[0], cant_vecinas[1]);
			cant_nearest = (int)Mat.lerp2d(cant, 
				cant_vecinas[0], cant_vecinas[1],
				cant * 4, cant * 2);
		}

		HashMap<Cicada,Float> nearest = cicada.nearest_neighbors(
				cicadas, cant_nearest, copia_filtros);

		ArrayList<Cicada> nearest_cics = new ArrayList<Cicada>(nearest.keySet());
		while (nearest_cics.size() > 0 && vecinas.size() < cant)
		{
			int rnd = Mat.rand_int( 0, nearest_cics.size() );
			Cicada vec = nearest_cics.remove(rnd);
			float dvec = nearest.get(vec);
			if (dvec < radio) vecinas.add( vec );
		}
		//System.out.println("\t pick vecinas "+cicada.toString()+" a "+vecinas);
		return vecinas;
	}	

	private boolean cutoff(Cicada cicada, 
					Collection<Cicada> cicadas,
					Population population)
	{
		boolean is_pop = population.is_pop( cicada );

		int pop_qty = population.canto_pop_qty();
		float pop_total_perc = (float)pop_qty / cicadas.size();
		//float pop_singers_perc = (float)pop_qty / population.cant_singers();
		if (pop_total_perc < 0.1)
		{
			//System.out.println("\t * do not cutoff, few cicadas");
			return false;
		}

		float e1 = 5.5f;
		float cut_notpop = (float)Math.pow((double)mass_coef[0], e1);
		if ( !is_pop && Mat.rand() < cut_notpop )
		{
			//System.out.println("\t * cutoff notpop "+cicada.canto_id());
			return true;
		}

		float e2 = 0.5f;
		float cut_pop = 1f-(float)Math.pow((double)mass_coef[0],1f/e2);
		if ( is_pop && Mat.rand() < cut_pop )
		{
			//System.out.println("\t * cutoff pop "+cicada.canto_id());
			return true;
		}

		return false;
	}
}
