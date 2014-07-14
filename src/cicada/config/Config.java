package cicada.config;

import java.util.ArrayList;
import java.util.HashMap;
import controlP5.*;
import chparsons.utils.*;
import chparsons.events.*;
import cicada.ui.*;
import cicada.events.*;
import cicada.cantos.*;

public class Config extends BaseConfig implements EventListener
{
	private ControlEventProcessor ctrl_event_processor;

	//cantos
	public ArrayList<String> cantos;
	public ArrayList<String> normales;
	public ArrayList<String> alertas;

	//comm
	public int broadcast_port;
	public int broadcaster_listening_port;
	public String broadcast_mode;
	public HashMap<String,String> arduinos;
	public int[] comm_max_frec; //ptr
	public int[] comm_send_0_frec; //ptr

	//ui
	public int win_w, win_h;
	public int map_w, map_slots;
	public int[] sliders_millis_range;
	public int sliders_width, sliders_height;

	//machos
	public int[] machos_tempo;
	public String[] machos_filtros;

	//cantos
	public int[] duracion_canto;

	//copias
	public int[] copia_tempo;
	public int[] copia_cant_vecinas;
	public int[] copia_radio;
	public String[] copia_filtros;

	public int[] copia_max_frec_change; //ptr
	public int[] copia_max_millis_change; //ptr
	public int[] copia_max_cant_keys_change; //ptr		

	//sincronizacion
	public float[] sinc_coef; //ptr
	public float[] mass_coef; //ptr
	public float[] sinc_speed; //ptr

	public Config()
	{
		super("cicada.cfg");

		ctrl_event_processor = new ControlEventProcessor();

		//cantos: nested base config
		
		BaseConfig cantos_config = new BaseConfig("cicada.cantos.cfg");
		normales = cantos_config.get_list("canto", Sep.PROP_KEY_ELEM);
		alertas = cantos_config.get_list("alerta", Sep.PROP_KEY_ELEM);
		cantos = new ArrayList<String>(normales);
		cantos.addAll(alertas);

		// ui
		
		int ws[]= get_range_int("window_size", "x");
		win_w = ws[0]; win_h = ws[1];
		map_w = get_int("map_width");
		map_slots = get_int("map_slots");
		sliders_millis_range = get_range_int("sliders_millis_range", 
				Sep.RANGE);
		sliders_width = get_int("sliders_width");
		sliders_height = get_int("sliders_height");

		// comm

		broadcast_port = get_int("broadcast_port");
		broadcaster_listening_port = get_int("broadcaster_listening_port");
		broadcast_mode = get_prop("broadcast_mode");
		arduinos = get_map("arduino", Sep.PROP_KEY_ELEM);
		//arduinos = get_list("arduino", Sep.PROP_KEY_ELEM);
		
		comm_max_frec = get_int_ptr("comm_max_frec");
		comm_send_0_frec = get_int_ptr("comm_send_0_frec");

		// sim

		//cantos = get_map("canto", Sep.PROP_KEY_ELEM);
		//cantos = get_list("canto", Sep.PROP_KEY_ELEM);
		
		machos_tempo = get_range_int("machos_tempo", Sep.RANGE);
		machos_filtros = get_list_str("machos_filtros", Sep.ELEM);

		duracion_canto = get_range_int("duracion_canto", Sep.RANGE);
		
		copia_tempo = get_range_int("copia_tempo", Sep.RANGE);
		copia_cant_vecinas = get_range_int("copia_cant_vecinas", 
				Sep.RANGE);
		copia_radio = get_range_int("copia_radio", Sep.RANGE);
		copia_filtros = get_list_str("copia_filtros", Sep.ELEM);

		copia_max_frec_change = get_int_ptr(
				"copia_max_frec_change");
		copia_max_millis_change = get_int_ptr(
				"copia_max_millis_change");
		copia_max_cant_keys_change = get_int_ptr(
				"copia_max_cant_keys_change");

		sinc_coef = get_float_ptr("sinc_coef");
		mass_coef = get_float_ptr("mass_coef");
		sinc_speed = get_float_ptr("sinc_speed");
	}		

	// events

	public void on_event(Object _e)
	{
		ctrl_event_processor.call_method(this, _e);
	}

	public void save_params()
	{
		save_params(null);
	}

	public void save_params(Controller ctrl)
	{
		System.out.println("Config save params");

		String sep = Sep.RANGE;

		set_prop("comm_max_frec",
			int_ptr_to_str(comm_max_frec) );
		set_prop("comm_send_0_frec",
			int_ptr_to_str(comm_send_0_frec) );

		set_prop("machos_tempo",
			int_range_to_str(machos_tempo,sep) );
		set_prop("duracion_canto",
			int_range_to_str(duracion_canto,sep) );
		
		set_prop("copia_tempo",
			int_range_to_str(copia_tempo,sep) );
		set_prop("copia_cant_vecinas",
			int_range_to_str(copia_cant_vecinas,sep) );
		set_prop("copia_radio",
			int_range_to_str(copia_radio,sep) );

		set_prop("copia_max_frec_change",
			int_ptr_to_str(copia_max_frec_change) );
		set_prop("copia_max_millis_change",
			int_ptr_to_str(copia_max_millis_change) );
		set_prop("copia_max_cant_keys_change",
			int_ptr_to_str(copia_max_cant_keys_change) );

		set_prop("sinc_coef",
			float_ptr_to_str(sinc_coef) );
		set_prop("mass_coef",
			float_ptr_to_str(mass_coef) );
		set_prop("sinc_speed",
			float_ptr_to_str(sinc_speed) );

		save("Cicada Config");
	}

	// ui widgets callbacks	

	public void range_machos_tempo(Controller ctrl)
	{
		float[] val = ((Range)ctrl).arrayValue();
		machos_tempo[0] = (int)val[0];
		machos_tempo[1] = (int)val[1];
	}
	
	public void range_duracion_canto(Controller ctrl)
	{
		float[] val = ((Range)ctrl).arrayValue();
		duracion_canto[0] = (int)val[0];
		duracion_canto[1] = (int)val[1];
	}
		
	public void range_copia_tempo(Controller ctrl)
	{
		float[] val = ((Range)ctrl).arrayValue();
		copia_tempo[0] = (int)val[0];
		copia_tempo[1] = (int)val[1];
	}
		
	public void range_copia_cant_vecinas(Controller ctrl)
	{
		float[] val = ((Range)ctrl).arrayValue();
		copia_cant_vecinas[0] = (int)val[0];
		copia_cant_vecinas[1] = (int)val[1];
	}
		
	public void range_copia_radio(Controller ctrl)
	{
		float[] val = ((Range)ctrl).arrayValue();
		copia_radio[0] = (int)val[0];
		copia_radio[1] = (int)val[1];
	}

	public void slider_sinc_coef(Controller ctrl)
	{
		sinc_coef[0] = ctrl.value();
	}

	public void slider_mass_coef(Controller ctrl)
	{
		mass_coef[0] = ctrl.value();
	}

	public void slider_sinc_speed(Controller ctrl)
	{
		sinc_speed[0] = ctrl.value();
	}

	public void numbox_copia_max_frec_change(Controller ctrl)
	{
		copia_max_frec_change[0] = (int)clamp_ctrl( ctrl, 
				0, Keypoint.MAX_FREC );
	}
	
	public void numbox_copia_max_millis_change(Controller ctrl)
	{
		copia_max_millis_change[0] = (int)clamp_ctrl(ctrl, 0, 999);
	}
	
	public void numbox_copia_max_cant_keys_change(Controller ctrl)
	{
		copia_max_cant_keys_change[0] = (int)clamp_ctrl(ctrl, 0, 20);
	}

	public void numbox_comm_max_frec(Controller ctrl)
	{
		comm_max_frec[0] = (int)clamp_ctrl(ctrl, 0, 9999);
	}

	public void toggle_comm_send_0_frec(Controller ctrl)
	{
		comm_send_0_frec[0] = (int)clamp_ctrl(ctrl, 0, 1);
	}

	private float clamp_ctrl(Controller ctrl, float min, float max)
	{
		float val = ctrl.value();
		if (val < min || val > max)
		{
			val = Mat.clamp( val, min, max );
			ctrl.setValue( val );
		}
		return val;
	}
}
