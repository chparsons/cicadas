package cicada.ui;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import controlP5.*;
import chparsons.events.*;
import chparsons.utils.Mat;
import cicada.config.*;
import cicada.events.*;
import cicada.Cicada;

public class UI extends EventDispatcher implements ControlListener, EventListener
{
	private PApplet p5;
	private Config config;
	private ControlP5 cp5;
	private Map map;
	private int map_y;
	private Widgets widgets;
	private Textlabel timer_txt;
	private Textlabel fps_txt;

	public UI(PApplet p5, Config config)
	{
		this.p5 = p5;
		this.config = config;
		
		cp5 = new ControlP5(p5);
		cp5.addListener(this);
		
		map = new Map(this, p5, cp5, config);
		map_y = 20;

		widgets = new Widgets(cp5);

		ControlFont fnt = new ControlFont(
				p5.createFont("Arial",12,false), 12);

		timer_txt = new Textlabel(p5, "", 0, 2, p5.width/2, 30);
		timer_txt.setControlFont(fnt);

		fps_txt = new Textlabel(p5, "", p5.width-100, 2, 100, 30);
		fps_txt.setControlFont(fnt);
	}

	public void init()
	{
		map.init( this, 0, map_y, config.map_w );

		int sliders_y = map_y + map.height() + 10;
		widgets.init( config, 0, sliders_y );
	}

	public void load_map()
	{
		map.load();
	}

	public void update(String time_str)
	{
		timer_txt.setValue(time_str);
		timer_txt.draw(p5);
		fps_txt.setValue( "fps: "+p5.frameRate );
		fps_txt.draw(p5);
	}

	public void controlEvent(ControlEvent e)
	{
		dispatch( e );
	}

	public void on_event(Object _e)
	{
		process_cicada_event(_e);
	}

	private void process_cicada_event(Object _e)
	{
		if ( !(_e instanceof CicadaEvent) )
			return;

		CicadaEvent e = (CicadaEvent)_e;
		
		int curr_mode = p5.g.colorMode;
		p5.colorMode(PConstants.HSB);

		process_update_event(e);	
		//process_macho_event(e);	
		//process_canto_event(e);	

		p5.colorMode(curr_mode);
	}

	private void process_update_event(CicadaEvent e)
	{
		if ( !e.type().equals(CicadaEvent.UPDATE) )
			return;

		Cicada c = e.cicada();
		
		float hue = Mat.lerp2d( (float)c.canto_id(), 
				0, config.cantos.size(), 0, 255 );

		if ( !c.singing() )
		{
			map.reset_slot_color( c.id() );
			return;
		}
			
		float val = 255;
		float a = c.age();
		//float a = c.is_macho() ? (((int)(age*100))%2>0?age:1) : age;
		float alfa = (1-a) * 180;

		map.set_slot_color( c.id(),
				p5.color(hue,val,val),
				p5.color(hue,val,val,alfa) );

		PVector loc = get_map_loc(c);	

		float r = 12;
		float[] time = Mat.polar2cart( 
				c.canto_time() * Mat.TWO_PI - Mat.HALF_PI, r);
		time[0] += loc.x;
		time[1] += loc.y;

		p5.stroke(hue, 255, 255, 255);
		p5.line( loc.x, loc.y, time[0], time[1] );
		p5.fill(255,0);
		p5.ellipse( loc.x, loc.y, r, r);

		if ( c.is_macho() )
		{
			p5.fill(255,0);

			int rmin = config.copia_radio[0] * 2;
			p5.stroke(hue, 255, 255, 60);
			p5.ellipse( loc.x, loc.y, rmin, rmin);

			int rmax = config.copia_radio[1] * 2;
			p5.stroke(hue, 255, 255, 120);
			p5.ellipse( loc.x, loc.y, rmax, rmax);
		}

		Cicada parent = c.get_parent();
		if ( parent != null )
		{
			PVector ploc = get_map_loc(parent);
			p5.stroke(hue, 255, 255, 255);
			p5.line( loc.x, loc.y, ploc.x, ploc.y );

			//parent pointing
			p5.fill(hue, 255, 255, 255);
			p5.ellipse( loc.x, loc.y, 5, 5);
			//float len = 10;
			//PVector p1 = new PVector();
			//p1.x = ploc.x > loc.x ? loc.x+len : loc.x-len;
			//p1.y = loc.y;
			//PVector p2 = new PVector();
			//p2.x = loc.x;
			//p2.y = ploc.y > loc.y ? loc.y+len : loc.y-len;
			//p5.triangle( loc.x, loc.y, 
					//p1.x, p1.y, 
					//p2.x, p2.y);
		}
	}	

	private PVector get_map_loc(Cicada c)
	{
		PVector loc = c.loc();
		//PVector maploc = map.loc();
		//loc.x += maploc.x;
		//loc.y += maploc.y;
		loc.x += 10;
		loc.y += 50;
		return loc;
	}

	//public String get_widget_name(String type, String id)
	//{
		//return type+"#"+id;
	//}		
}
