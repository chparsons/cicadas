package cicada.ui;

import processing.core.PApplet;
import controlP5.*;
import chparsons.utils.*;
import cicada.*;
import cicada.events.*;
import cicada.config.*;

public class Widgets
{
	private ControlP5 cp5;

	int slider_w, slider_h;
	int num_w, num_h;
	int toggle_s;
	int curr_x, curr_y;
	int yoff;

	public Widgets(ControlP5 cp5)
	{
		this.cp5 = cp5;
	}

	public void init(Config config, int _x, int _y)
	{
		slider_w = config.sliders_width;
		slider_h = config.sliders_height;

		num_w = 100;
		num_h = 15;

		toggle_s = 10;

		yoff = 10;
		
		curr_x = _x;
		curr_y = _y;

		//buttons

		int cant_btns = 4;
		int bt_h = 20;
		int bt_w = config.win_w / cant_btns;
		add_button("save_params", bt_w, bt_h, cant_btns);
		add_button("silence_all_cicadas", bt_w, bt_h, cant_btns);
		add_button("play", bt_w, bt_h, cant_btns);
		add_button("stop", bt_w, bt_h, cant_btns);
		curr_y += bt_h + yoff;
		curr_x = _x;

		//sliders
		
		add_slider("slider_sinc_speed",
				new float[] { 0f, 1f }, 
				config.sinc_speed);

		Slider sc = add_slider("slider_sinc_coef",
				new float[] { 0f, 1f }, 
				config.sinc_coef);
		sc.setBehavior( new SincBehavior(config) );
		

		Slider mc = add_slider("slider_mass_coef",
				new float[] { 0f, 1f }, 
				config.mass_coef);
		mc.setBehavior( new SincBehavior(config) );

		//ranges
		
		add_range("range_duracion_canto", 
				config.sliders_millis_range, 
				config.duracion_canto);

		add_range("range_machos_tempo", 
				new int[] { 1000, 30000 }, 
				config.machos_tempo);	
		
		add_range("range_copia_tempo", 
				config.sliders_millis_range, 
				config.copia_tempo);	

		add_range("range_copia_radio", 
				new int[] { 1, 1000 }, 
				config.copia_radio);

		add_range("range_copia_cant_vecinas", 
				new int[] { 1, 10 }, 
				config.copia_cant_vecinas);
	
		//numbers

		add_numbox("numbox_copia_max_frec_change",
				config.copia_max_frec_change);

		add_numbox("numbox_copia_max_millis_change",
				config.copia_max_millis_change);

		//add_numbox("numbox_copia_max_cant_keys_change",
				//config.copia_max_cant_keys_change);

		add_numbox("numbox_comm_max_frec",
				config.comm_max_frec);

		add_numbox("toggle_comm_send_0_frec",
				config.comm_send_0_frec);

		curr_x = _x;
	}

	private Button add_button(String id, int bt_w, int bt_h, int cant_btns)
	{
		int bt_sep = 10;
		Button bt = cp5.addButton( id, 0, 
				curr_x, curr_y, bt_w - bt_sep, bt_h );
		curr_x += bt_w;
		return bt;
	}

	private Range add_range(String id, int[] range, int[] initrange)
	{
		Range r = cp5.addRange( id,
				range[0], range[1],
				initrange[0], initrange[1],
				curr_x, curr_y, slider_w, slider_h);
		r.setLabel( parse_label(id) );
		curr_y += slider_h + yoff;
		return r;
	}	

	private Slider add_slider(String id, float[] range, float[] initvalue)
	{
		Slider s = cp5.addSlider( id,
				range[0], range[1], initvalue[0],
				curr_x, curr_y, slider_w, slider_h);
		s.setLabel( parse_label(id) );
		curr_y += slider_h + yoff;
		return s;
	}

	private Numberbox add_numbox(String id, int[] initvalue)
	{
		Numberbox n = cp5.addNumberbox( id,
				initvalue[0],
				curr_x, curr_y, num_w, num_h);
		n.setLabel( parse_label(id) );
		curr_x += num_w + 80;
		return n;
	}

	//private Toggle add_toggle(String id, int[] initvalue)
	//{
		//Toggle t = cp5.addToggle(id);
		//t.setValue( initvalue[0] );
		//t.setSize(toggle_s,toggle_s);
		//curr_x += toggle_s + 80;
		//return t;
	//}

	private String parse_label(String str)
	{
		return str.substring( str.indexOf("_") + 1 );
	}

	class SincBehavior extends ControlBehavior
	{
		private Config config;
		private float pi = (float)Math.PI;
		private float t;

		public SincBehavior(Config config) 
		{	
			this.config = config;
			t = 0;
		}

		public void update() 
		{
			if ( Timer.paused() )
				return;

			if ( Engine.state().equals(StateEvent.ALERTA) )
			{
				t = -pi * .5f;
			}
			else
			{
				float speed = config.sinc_speed[0] * 0.0015f;
				if (speed == 0) 
					return;
				t += speed;
			}

			setValue( (PApplet.sin(t) + 1 ) * .5f );

			if (t > pi) t = -pi;
		}
	}
}
