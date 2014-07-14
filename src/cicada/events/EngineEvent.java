package cicada.events;

public class EngineEvent
{
	static public final String SILENCE_ALL = "silence_all_cicadas";

	private String _type;

	public EngineEvent(String type)
	{
		_type = type;
	}

	public String type()
	{
		return _type;
	}
}
