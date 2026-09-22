package com.okworx.ilcd.validation.tool.logging;

import java.io.Serializable;
import java.util.concurrent.locks.*;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "BufferedAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public final class BufferedAppender extends AbstractAppender {

	private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final Lock readLock = rwLock.readLock();

	protected BufferedAppender(String name, Filter filter, Layout<? extends Serializable> layout,
			final boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions, new Property[] {});
	}

	@Override
	public void append(LogEvent event) {
		readLock.lock();
		try {
			// final byte[] bytes = getLayout().toByteArray(event);
			BufferedLog.getInstance().appendToBuffer(event.getLevel() + " " + event.getSource().getClassName() + " "
					+ event.getMessage().getFormattedMessage() + "\n");
		} catch (Exception ex) {
			if (!ignoreExceptions()) {
				throw new AppenderLoggingException(ex);
			}
		} finally {
			readLock.unlock();
		}
	}

	@PluginFactory
	public static BufferedAppender createAppender(@PluginAttribute("name") String name,
			@PluginElement("Layout") Layout<? extends Serializable> layout,
			@PluginElement("Filter") final Filter filter, @PluginAttribute("otherAttribute") String otherAttribute) {
		if (name == null) {
			LOGGER.error("No name provided for BufferedAppender");
			return null;
		}
		if (layout == null) {
			layout = PatternLayout.createDefaultLayout();
		}
		return new BufferedAppender(name, filter, layout, true);
	}

}
