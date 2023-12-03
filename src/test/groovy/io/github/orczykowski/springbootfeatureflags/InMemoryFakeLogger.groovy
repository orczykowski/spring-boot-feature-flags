package io.github.orczykowski.springbootfeatureflags

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender

class InMemoryFakeLogger extends ListAppender<ILoggingEvent> {
    def reset() {
        this.list.clear()
    }

    def hasLog(String msg, Level level) {
        assert this.list.find {
            it.formattedMessage == msg
                    && it.level == level
        } != null
        true
    }
}
