package com.oviktor.annotation.handler;

import com.oviktor.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

import static com.oviktor.connection.DbConnections.getCurrentThreadConnection;
import static com.oviktor.connection.DbConnections.invalidateConnection;


/**
 * {@link com.oviktor.annotation.handler.TransactionalInvocationHandler} is a class that implements
 * the {@link java.lang.reflect.InvocationHandler} interface from the java.lang.reflect.
 * InvocationHandler is used in Java to create dynamic proxy objects.
 * Proxy objects are objects that can be used to intercept method calls
 * that have been made to a real object.
 * @param <T> object of a real service
 */
@Slf4j
public class TransactionalInvocationHandler<T> implements InvocationHandler {

    private final T realService;

    public TransactionalInvocationHandler(T service) {
        this.realService = service;
    }

    /**
     * {@link com.oviktor.annotation.handler.TransactionalInvocationHandler#invoke(Object proxy, Method method, Object[] args)}
     * method to which all calls to the proxy object are routed
     *
     * @param proxy object allows to provide an intermediate link between the calling code
     * and the real object. This can be useful, for example, when you need to execute some additional
     * logic around an object method call, such as access rights checking, transaction management, etc.
     *
     * @param method the {@code Method} instance corresponding to
     * the interface method invoked on the proxy instance.  The declaring
     * class of the {@code Method} object will be the interface that
     * the method was declared in, which may be a superinterface of the
     * proxy interface that the proxy class inherits the method through.
     *
     * @param args an array of objects containing the values of the
     * arguments passed in the method invocation on the proxy instance,
     * or {@code null} if interface method takes no arguments.
     * Arguments of primitive types are wrapped in instances of the
     * appropriate primitive wrapper class, such as
     * {@code java.lang.Integer} or {@code java.lang.Boolean}.
     *
     * @return
     * @throws Throwable
     */

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method m = realService.getClass().getMethod(method.getName(), method.getParameterTypes());
        if (m.isAnnotationPresent(Transactional.class)) {
            Connection connection = null;
            try {
                connection = getCurrentThreadConnection();
                connection.setAutoCommit(false);
                Object invoke = m.invoke(realService, args);
                connection.commit();
                return invoke;
            } catch (Exception e) {
                log.error("Something went wrong during the execution of the transactional method. Attempting to rollback changes");
                try {
                    if (connection != null) {
                        connection.rollback();
                        log.info("Successful rollback");
                    }
                } catch (Exception ex) {
                    log.error("Rollback failed. More details :",ex);
                    throw new RuntimeException();
                }
                log.error("Transaction within current connection failed",e);
                throw new RuntimeException();
            } finally {
                invalidateConnection();
            }
        }
        return m.invoke(realService, args);
    }
}
