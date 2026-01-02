package de.ostfale.va.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface UseLogging {

    Map<Class<?>, Logger> LOGGER_CACHE = new ConcurrentHashMap<>();
    StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    static Logger staticLog() {
        Class<?> callerClass = STACK_WALKER.getCallerClass();
        return LOGGER_CACHE.computeIfAbsent(callerClass, LoggerFactory::getLogger);
    }

    default Logger log() {
        return LOGGER_CACHE.computeIfAbsent(getClass(), LoggerFactory::getLogger);
    }

    default Logger log(Class<?> clazz) {
        return LOGGER_CACHE.computeIfAbsent(clazz, LoggerFactory::getLogger);
    }
}
