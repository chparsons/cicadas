import processing.core.PApplet;
import cicada.*;
import cicada.ui.*;
import cicada.comm.*;
import cicada.config.*;

public class Main extends PApplet 
{
	private static final long serialVersionUID = 6947222662054777583L;

	public static void main(String[] args) 
	{
		PApplet.main(new String[] { "Main" });
	}

	private Config config;
	private Timer timer;
	private Engine engine;
	private UI ui;
	private Comm comm;
	
	public void setup()
	{
		System.out.println("Cicada Engine rev 033");

		config = new Config();

		size( config.win_w, config.win_h );
		frameRate(30);
		background(0);
	
		Timer.init();

		engine = new Engine(config);
		ui = new UI(this, config);
		comm = new Comm(this, config);

		ui.add_listener(engine);
		ui.add_listener(config);
		engine.add_listener(ui);
		comm.add_listener(engine);

		ui.init();
		ui.load_map();
		engine.init();
		comm.init();
	}

	public void draw()
	{
		background(0);
		Timer.update();
		engine.update();
		ui.update( engine.get_time_str() );
		comm.update( engine.get_cicadas() );
	}

	public void exit()
	{
		//config.save_params();
		engine.silence_all_cicadas(null);
		engine.update();
		comm.update( engine.get_cicadas() );
		super.exit();
	}

	public void keyPressed()
	{
		if (keyCode == 32)
			Timer.toggle();
	}

	//public void oscEvent(OscMessage msg)
	//{
	//}
	
	//public void controlEvent(ControlEvent e)
	//{
		//System.out.println("main controlEvent "+e.name()+", group: "+e.isGroup());
	//}
}
