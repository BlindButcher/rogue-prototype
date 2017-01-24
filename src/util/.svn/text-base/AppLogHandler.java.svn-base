package util;

import com.google.inject.Inject;
import main.Messages;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Application log handler for putting messages to player screen.
 *
 * @author Blind
 */
public class AppLogHandler extends Handler
{
    private Messages messages;

    @Inject
    public AppLogHandler(Messages messages)
    {
        this.messages = messages;
    }

    @Override
    public void publish(LogRecord record)
    {
        messages.addMessage(record.getMessage());
    }

    @Override
    public void flush()
    {
    }

    @Override
    public void close() throws SecurityException
    {
    }
}
