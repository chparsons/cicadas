package cicada.events;

public class StateEvent
{
	static public final String START = "state_start";
	static public final String STOP = "state_stop";
	static public final String SILENCE = "state_silence";
	static public final String ALERTA = "state_alerta";
	static public final String NORMAL = "state_normal";

	private String _type;

	public StateEvent(String type)
	{
		_type = type;
	}

	public String type()
	{
		return _type;
	}

	static public String make_type(String str)
	{
		return "state_"+str;
	}
}
