package cicada.behaviors;

import java.util.ArrayList;
import chparsons.utils.Mat;
import cicada.cantos.*;
import cicada.config.*;

public class Sincronizador implements Configurable
{	
	private float[] sinc_coef;
	private int[] max_frec_change;
	private int[] max_millis_change; 
	private int[] max_cant_keys_change;

	public Sincronizador() {}

	public Configurable set_config(Config config)
	{
		this.sinc_coef = config.sinc_coef;
		this.max_frec_change = config.copia_max_frec_change;
		this.max_millis_change = config.copia_max_millis_change;
		this.max_cant_keys_change = config.copia_max_cant_keys_change;
		return this;
	}

	public void phase(Canto canto, float time)
	{
		//double e = 2d;
		//float balance = (float)Math.pow( (double)sinc_coef[0], e );
		float balance = sinc_coef[0]; 
		float t = Mat.lerp( canto.time(), time, balance );
		canto.sync_time( t );
	}

	public Canto process(Canto canto)
	{
		//return new Canto( canto.id(), new ArrayList<Keypoint>() );
		
		float change_prob = 1-sinc_coef[0];

		ArrayList<Keypoint> keys = canto.get_keys();
		//System.out.println( canto.id()+" "+keys );
		
		keys = change_keys( keys, change_prob );
		//System.out.println( "\t"+canto.id()+" "+keys );

		//keys = change_keys_cant( keys, change_prob );
		//System.out.println( "\t"+canto.id()+" "+keys );

		return new Canto( canto.id(), keys );
	}

	private ArrayList<Keypoint> change_keys(ArrayList<Keypoint> src,
						float change_prob)
	{	
		ArrayList<Keypoint> dst = new ArrayList<Keypoint>();
		for (int i = 0; i < src.size(); i++)
		{
			Keypoint k = src.get(i);
			dst.add( change_key(k, change_prob) );
		}
		return dst;
	}

	//private ArrayList<Keypoint> change_keys_cant(ArrayList<Keypoint> src,
						//float change_prob)
	//{
		////TODO chequear cuantos keys tiene el canto original
		////y no desviarse (sumar o restar) mas de 1 key con respecto
		////al original

		//ArrayList<Keypoint> dst = new ArrayList<Keypoint>(src);

		//if (src.size() < 2) return dst;

		//for (int i = 0; i < max_cant_keys_change[0]; i++)
		//{
			//if ( Mat.rand() < change_prob )
			//{
				//int rnd = Mat.rand_int(0,dst.size());
				//if (Mat.rand() < 0.5)
				//{
					//dst.remove(rnd);
					//if (dst.size() < 2)
						//return dst;
				//}
				//else
				//{
					//Keypoint k = dst.get(rnd);
					//dst.add( change_key(k, change_prob) );
				//}
			//}
		//}
		//return dst;
	//}

	private Keypoint change_key(Keypoint src, float change_prob)
	{
		float _max_f = (float)max_frec_change[0];
		int frec_max_delta = (int)(_max_f * change_prob);

		float _max_m = (float)max_millis_change[0];
		int millis_max_delta = (int)(_max_m * change_prob);

		int frec_delta = Mat.rand_int(frec_max_delta);
		int millis_delta = Mat.rand_int(millis_max_delta);

		//dont change if its a silence...
		int frec = src.frec();
		if (frec > 0) frec = Mat.rand_around( frec, frec_delta );

		int millis = Mat.rand_around( src.millis(), millis_delta );

		return new Keypoint(frec, millis);
	}
}
