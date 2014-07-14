package cicada.ui;

import java.util.HashMap;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;
import controlP5.*;
import chparsons.utils.*;
import cicada.events.*;
import cicada.ui.*;
import cicada.config.*;

public class Map implements ControlListener
{
	private UI ui;
	private PApplet p5;
	private ControlP5 cp5;
	private Config config;
	
	private BaseConfig map;
	
	private ArrayList<String> ard_ids;
	private int _height;

	private int nslots;
	private HashMap<String,Toggle> toggles_by_value; //<ard_id:pin,toggle>
	
	private ControlGroup g;
	private CheckBox checkbox;

	public Map(UI ui, PApplet p5, ControlP5 cp5, Config config)
	{
		this.ui = ui;
		this.p5 = p5;
		this.cp5 = cp5;
		this.config = config;

		this.nslots = config.map_slots;
		toggles_by_value = new HashMap<String,Toggle>();

		ard_ids = new ArrayList<String>(config.arduinos.keySet());
		p5.registerMouseEvent(this);

		map = new BaseConfig("cicada.map.cfg");
	}

	public void init(UI ui, int _x, int _y, int _width)
	{
		int slot_xoff = 5;
		int slot_yoff = 12;
		int toggle_s = 10;
		int label_w = 22;
		int margin = 10;
		int bar_h = 20;

		int item_w = toggle_s + label_w + slot_xoff;
		int item_h = toggle_s + slot_yoff;
		int rows = (int)((_width-margin) / item_w);
		int cols = (int)(nslots / rows);
		
		int gx = _x;
		int gy = _y + bar_h;

		//int _panel_height = p5.height - bar_h*2 - _y;
		int _panel_height = (cols + 1) * item_h;
		_height = _panel_height + bar_h*2; //header and save bt bars

		g = cp5.addGroup("cicadas_map_g",
				gx, gy, _width);
		g.setLabel("map");
		g.setMoveable(false);
		g.setBackgroundHeight( _panel_height );
		g.setBarHeight( bar_h );
		g.setBackgroundColor( p5.color(20,0) );
		g.close();
		
		// slots
		checkbox = cp5.addCheckBox("cicadas_map_cb", 
				margin, margin);
		checkbox.setGroup(g);
		checkbox.setColorForeground( p5.color(120) );
		checkbox.setColorActive( p5.color(255) );
		checkbox.setColorLabel( p5.color(128) );
		checkbox.setItemsPerRow( rows );
		//checkbox.setItemHeight(11);
		//checkbox.setItemWidth(11);
		checkbox.setSpacingColumn( label_w + slot_xoff );
		checkbox.setSpacingRow( slot_yoff );
		
		// add slots
		for (int i = 0; i < nslots; i++)
		{
			String istr = Integer.toString(i);
			String tname = "toggle#"+istr;

			Toggle t = checkbox.addItem(tname, i);
			t.captionLabel().toUpperCase(false);
			t.setLabel("");
			t.setId(i);
			t.setWidth(toggle_s);
			t.setHeight(toggle_s);

			t.addListener(ui); //force UI to listen to the toggles
			//t.addListener(this);

			String txtname = get_txt_name(i);
			CVector3f tloc = t.position();
			Textfield txt = cp5.addTextfield(txtname,
					margin + (int)tloc.x + toggle_s, 
					margin + (int)tloc.y, 
					label_w, toggle_s);
			txt.captionLabel().toUpperCase(false);
			txt.setId(i);
			txt.setLabel("");
			txt.setText("");
			reset_slot_color(txt);
			txt.setGroup(g);
			txt.setWidth(label_w);
			txt.setHeight(toggle_s);
			//txt.setAutoClear(false);
			txt.addListener(this); //listen to enter key
		}

		// map

		int cant_bts = 1;

		int bt_sep = 0;
		int bt_width = _width/cant_bts;

		Button savebt = cp5.addButton("save_map", 0, 
			0, _panel_height, bt_width - bt_sep, bar_h);
		savebt.plugTo(this);
		savebt.setGroup(g);
		savebt.setMoveable(false);

		//Button shufflebt = cp5.addButton("shuffle_map", 0, 
			//bt_width, _panel_height, bt_width - bt_sep, bar_h);
		//shufflebt.plugTo(this);
		//shufflebt.setGroup(g);
		//shufflebt.setMoveable(false);
	}	

	public void load()
	{
		System.out.println("load map");
		remove_all_slots();
		for (int i = 0; i < nslots; i++)
		{
			String value = map.get_prop("slot_"+i);
			init_slot(i, value);	
		}
	}
	
	private void save_map()
	{
		System.out.println("save map");
		for (int i = 0; i < nslots; i++)
		{
			String value = get_slot_value(i);
			map.set_prop("slot_"+i, value);
		}
		map.save("Cicada Map");
	}	

	private void shuffle_map()
	{
		//TODO remove all cicadas from map
		ui.dispatch( new EngineEvent(EngineEvent.SILENCE_ALL) );
		remove_all_slots();

		//ArrayList<Integer> slots = new ArrayList<Integer>();
		//for (int i = 0; i < nslots; i++)
			//slots.add(i);

		//for (int i = 0; i < 100; i++)
		//{
			//int rnd_slot = Mat.rand_int(0,slots.size());
			//int nslot = slots.remove(rnd_slot); 

			//int _rnd_ard_id = Mat.rand_int(0,ard_ids.size());
			//String rnd_ard_id = ard_ids.get(_rnd_ard_id);
			//int rnd_ard_pin = Mat.rand_int(0,20);
			//String value = rnd_ard_id+Sep.ARD_ID_PIN+rnd_ard_pin;

			//init_slot(nslot, value);	
		//}
	}

	private void init_slot(int nslot, String value)
	{
		if ( ! set_slot_value( nslot, value ) 
				|| StringUtil.is_empty(value) )
			return;
		Textfield txt = get_txt(nslot);
		Toggle t = get_toggle(nslot);
		txt.submit();
		//t.toggle();
		t.setState(true);
	}

	private void remove_all_slots()
	{
		for (int i = 0; i < nslots; i++)
			remove_slot(i);
	}

	private void remove_slot(int nslot)
	{
		//Toggle t = get_toggle(nslot);
		//Textfield txt = get_txt(nslot);
		set_slot_value(nslot, "");
		//t.setState(false);
	}

	//public PVector loc() 
	//{
		//CVector3f _loc = g.position();
		//return new PVector(_loc.x, _loc.y, _loc.z);
	//}

	public int height()
	{
		return _height;
	}

	private int get_nslot(Controller ctrl)
	{
		return ctrl.id();
	}

	private String get_slot_value(int nslot)
	{
		return get_slot_value( get_toggle(nslot) );
	}

	private String get_slot_value(Toggle t)
	{
		return t.label();
	}

	private boolean set_slot_value(int nslot, String value)
	{
		boolean empty = StringUtil.is_empty(value);

		if ( !empty && !is_slot_input_valid(value) ) 
			return false;

		Toggle t = get_toggle(nslot);
		String curr_value = get_slot_value(t);

		toggles_by_value.put( curr_value, null );
		toggles_by_value.put( value, empty ? null : t );
		
		t.setLabel(value);

		return true;
	}

	public void set_slot_color(String cicada_id, 
			int forecolor, int backcolor)
	{
		Toggle t = get_toggle_by_value(cicada_id);
		Textfield txt = get_txt( get_nslot(t) );
		set_slot_color(txt, forecolor, backcolor);
	}

	private void set_slot_color(Textfield txt, 
			int forecolor, int backcolor)
	{
		txt.setColorForeground( forecolor );
		txt.setColorBackground( backcolor );
		txt.setColorCaptionLabel( p5.color(255) );
	}

	public void reset_slot_color(String cicada_id)
	{
		Toggle t = get_toggle_by_value(cicada_id);
		Textfield txt = get_txt( get_nslot(t) );
		reset_slot_color(txt);
	}

	private void reset_slot_color(Textfield txt)
	{
		set_slot_color(txt, p5.color(40), p5.color(255,0));
	}	

	private Toggle get_toggle_by_value(String cicada_id)
	{
		return toggles_by_value.get(cicada_id);
	}

	private Toggle get_toggle(int nslot)
	{
		return checkbox.getItem(nslot);
	}

	private Textfield get_txt(int nslot)
	{
		String name = get_txt_name(nslot);
		return (Textfield)cp5.controller(name);
	}

	private Textfield get_txt_on(int absx, int absy)
	{
		for (int i = 0; i < nslots; i++)
		{
			Textfield txt = get_txt(i);
			CVector3f txtloc = txt.absolutePosition();
			int txtw = txt.getWidth();
			int txth = txt.getHeight();
			if ( absx > (int)txtloc.x && absx < (int)txtloc.x+txtw
			&& absy > (int)txtloc.y && absy < (int)txtloc.y+txth)
			{
				return txt;
			}
		}
		return null;

	}

	private void clear_all_txts()
	{
		for (int i = 0; i < nslots; i++)
			get_txt(i).clear();
	}

	private Textfield get_focused_txt()
	{
		for (int i = 0; i < nslots; i++)
		{
			Textfield txt = get_txt(i);
			if ( txt.isFocus() )
				return txt;
		}
		return null;
	}

	private String get_txt_name(int nslot)
	{
		return "cicada_slot_txt#"+nslot;
	}

	private boolean is_slot_input_valid(String value)
	{
		if (value.indexOf(Sep.ARD_ID_PIN) == -1)
		{
			System.out.println("Map Error: invalid slot input, "+Sep.ARD_ID_PIN+" char is missing");
			return false;
		}

		String[] input_idpin = value.split(Sep.ARD_ID_PIN);
		String input_id = input_idpin[0];
		String input_pin = input_idpin[1];
		if (ard_ids.indexOf( input_id ) == -1)
		{
			System.out.println("Map Error: invalid slot input, arduino id "+input_id+" not found in arduinos list from config file");
			return false;
		}

		try
		{
			Integer.parseInt(input_pin);
		}
		catch (Exception e) 
		{	
			System.out.println("Map Error: invalid slot input, arduino pin "+input_pin+" is not an integer");
			return false; 
		}

		if ( is_slot_value_assigned(value) )
		{
			System.out.println("Map Error: invalid slot input, "+value+" is already assinged to another slot");
			return false;
		}

		return true;
	}

	private boolean is_slot_value_assigned(String value)
	{
		return toggles_by_value.get(value) != null;
	}


	// events
	

	public void mouseEvent(MouseEvent e) 
	{
		if (e.getID() == MouseEvent.MOUSE_PRESSED)
		{
			process_mouse_on_txt(e);
			//Textfield txt = get_focused_txt();
			//if (txt != null) txt.submit();
			clear_all_txts();
		}
	}

	private void process_mouse_on_txt(MouseEvent e)
	{
		Textfield txt = get_txt_on(e.getX(), e.getY());
		if (txt == null) 
			return;
		int nslot = get_nslot(txt);
		Toggle t = get_toggle(nslot);
		if (t.getState())
		{
			txt.setFocus(false);
			return;
		}
		set_slot_value(nslot, "");
	}

	public void controlEvent(ControlEvent e)
	{
		//CicadaEvent.print("Map", e);
		process_txt_event(e);
		//process_toggle_event(e);
	}

	//private void process_toggle_event(ControlEvent e)
	//{
		//try
		//{
			//Toggle t = (Toggle)e.controller(); 
		//}
		//catch(Exception err){}
	//}

	private void process_txt_event(ControlEvent e)
	{
		try
		{
			Textfield txt = (Textfield)e.controller(); 
			set_slot_value( get_nslot(txt), txt.getText() );
		}
		catch(Exception err){}
	}
}

