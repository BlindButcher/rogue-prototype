package main;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

public class Messages
{
	private List<String> messages = new ArrayList<String>();
	private EventHolder eventHolder;

	@Inject
	public Messages(EventHolder eventHolder)
	{
		this.eventHolder = eventHolder;
	}

	public void addMessage(String message)
	{
		messages.add(eventHolder.time + " : " + message);
	}

	public List<String> getMessages(int count)
	{
		List<String> result = new ArrayList<String>();
		for (int i = messages.size() - 1, c = 0; i >= 0 && c < count; i--, c++)
			result.add(messages.get(i));
		return result;
	}

}
