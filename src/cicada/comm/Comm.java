package cicada.comm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import processing.core.PApplet;
import oscP5.*;
import netP5.*;
import chparsons.utils.*;
import chparsons.events.*;
import cicada.*;
import cicada.config.*;
import cicada.events.*;
import cicada.cantos.*;

public class Comm extends EventDispatcher implements OscEventListener
{
	private final String PATTERN_SEND = "/cicada";
	private final String PATTERN_RECEIVE_CONTROL = "/cicada/control";

	private Config config;
	
	private OscP5 oscP5;
	private NetAddressList addrs;

	private NetAddress osctest_addr;
	private String osctest_id = "osctest";

	private int broadcast_port;
	private String broadcast_mode;

	public Comm(PApplet p5, Config config)
	{
		this.config = config;

		if (config.broadcast_port == config.broadcaster_listening_port)
		{
			System.out.println("Comm Error, broadcast_port and broadcaster_listening_port have the same port set to "+config.broadcast_port);
			return;
		}			

		addrs = new NetAddressList();
		oscP5 = new OscP5(p5, config.broadcaster_listening_port);
		System.out.println();

		oscP5.addListener(this);
		broadcast_port = config.broadcast_port;
		broadcast_mode = config.broadcast_mode;

	}

	public void init()
	{
		if (oscP5 == null) return;

		//TODO comm: implement message connection pattern?

		Iterator it = config.arduinos.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry m = (Map.Entry)it.next();
			String ard_id = (String)m.getKey();
			String ip = (String)m.getValue();
			System.out.println("Cicada Comm add arduino "+ard_id+" ip "+ip);
			NetAddress addr = new NetAddress(ip, broadcast_port);
			addrs.add(addr);
		}

		String test_ip = config.arduinos.get(osctest_id);
		if (test_ip != null)
			osctest_addr = new NetAddress(test_ip, broadcast_port);

		//arduino_1=192.168.0.101@1,2,3,4,5
		//for (String ard : config.arduinos)
		//{
			//String[] data = ard.split(Sep.ARD_IP_IDPIN);
			//String ip = data[0];
			//System.out.println("Cicada Comm add arduino "+ip);
			//NetAddress addr = new NetAddress(ip, broadcast_port);
			//addrs.add(addr);
		//}
	}

	public void update(Collection<Cicada> cicadas)
	{
		if (oscP5 == null) return;

		//System.out.println("Cicada Comm update "+cicadas);
		for (Cicada c : cicadas)
		{
			if ( config.comm_send_0_frec[0] == 0 && !c.singing() )
				continue;

			int frec = config.comm_max_frec[0]==Keypoint.MAX_FREC ?
				c.frec() : 
				(int)Mat.lerp2d( (float)c.frec(),
					0, (float)Keypoint.MAX_FREC,
					0, (float)config.comm_max_frec[0] );

			String[] _idpin = c.id().split(Sep.ARD_ID_PIN);
			String ard_id = _idpin[0];
			String ard_pin = _idpin[1];

			OscMessage msg = new OscMessage(PATTERN_SEND);
			msg.add( ard_id );
			msg.add( ard_pin );
			msg.add( frec );

			//System.out.println("\t Cicada Comm broadcast "+msg+", id: "+msg.get(0).intValue()+", frec: "+msg.get(1).intValue());
			if (broadcast_mode.equals("all"))
			{
				oscP5.send(msg, addrs);
			}
			else if (broadcast_mode.equals("selective"))
			{
				NetAddress addr = get_addr( ard_id );
				if (addr != null)
					oscP5.send(msg, addr);
			}

			if (osctest_addr != null)
				oscP5.send(msg, osctest_addr);
		}
	}

	private NetAddress get_addr(String arduino_id)
	{
		String ip = config.arduinos.get(arduino_id);
		if (ip == null)
		{
			System.out.println("Comm couldnt get ip for arduino "+arduino_id);
			return null;
		}
		return addrs.get(ip, broadcast_port);

		//Iterator it = config.arduinos.entrySet().iterator();
		//while (it.hasNext())
		//{
			//Map.Entry m = (Map.Entry)it.next();
			//String ard_id = (String)m.getKey();
			//String ip = (String)m.getValue();
			//if ( ard_id.equals( arduino_id ) )
				//return addrs.get(ip, broadcast_port);
		//}

		//arduino_1=192.168.0.101@1,2,3,4,5
		//for (String ard : config.arduinos)
		//{
			//String[] data = ard.split(Sep.ARD_IP_IDPIN);
			//String ip = data[0];
			//String[] idpins = data[1].split(Sep.ELEM);
			//// a list e.g. 1,2,3,4,5
			//for (String _idpin : idpins)
				//if ( _idpin.equals( cicada_id ) )
					//return addrs.get(ip, broadcast_port);

			////String sep_range = Sep.RANGE;
			////// a range e.g. 1_5
			////if (data[1].indexOf(sep_range) != -1)
			////{
				////String[] ids = data[1].split(sep_range);
				////int min = Integer.parseInt(ids[0]);
				////int max = Integer.parseInt(ids[1]);
				////if (id >= min && id <= max)
					////return addrs.get(ip, broadcast_port);
			////}
		//}

		//return null;
	}

	public void oscEvent(OscMessage msg) 
	{
		//System.out.println("### Cicada Comm received osc message, addrpattern: "+msg.addrPattern()+", typetag: "+msg.typetag());
		//msg.print();

		if (msg.addrPattern().equals(PATTERN_RECEIVE_CONTROL))
		{
			String incoming = msg.get(0).stringValue();
			String type = StateEvent.make_type(incoming);
			dispatch( new StateEvent(type) );
		}
	}

	public void oscStatus(OscStatus status)
	{
		System.out.println("### Cicada Comm osc status "+status.id());
	}
}
