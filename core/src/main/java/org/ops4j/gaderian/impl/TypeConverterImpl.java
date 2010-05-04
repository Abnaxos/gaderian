package org.ops4j.gaderian.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.impl.types.StringConstructorHandler;
import org.ops4j.gaderian.internal.Module;
import org.ops4j.gaderian.internal.RegistryInfrastructure;
import org.ops4j.gaderian.util.Defense;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TypeConverterImpl implements TypeConverter
{

    static final String TYPE_HANDLERS = "gaderian.TypeHandlers";

    private static final TypeHandler UNSUPPORTED = new TypeHandler() {
        public Object stringToObject(Module module, String input, Location location) throws Exception {
            throw new UnsupportedOperationException();
        }
    };

//    private final Map<Class<?>, Class<?>> _primitives;
    private final RegistryInfrastructure _registry;
    private final AtomicReference<ConcurrentMap<Class<?>, TypeHandler>> _handlers = new AtomicReference<ConcurrentMap<Class<?>, TypeHandler>>();

    public TypeConverterImpl( RegistryInfrastructure registry )
    {
        _registry = registry;
//        Map<Class<?>, Class<?>> primitives = new HashMap<Class<?>, Class<?>>(7);
//        primitives.put(int.class, Integer.class);
//        primitives.put(long.class, Long.class);
//        primitives.put(short.class, Short.class);
//        primitives.put(byte.class, Byte.class);
//        primitives.put(double.class, Double.class);
//        primitives.put(float.class, Float.class);
//        primitives.put(boolean.class, Boolean.class);
//        _primitives = Collections.unmodifiableMap(primitives);
    }

    @SuppressWarnings({ "unchecked" })
    private synchronized ConcurrentMap<Class<?>, TypeHandler> getHandlers()
    {
        if( _handlers.get() == null ) {
            _handlers.compareAndSet(null, new ConcurrentHashMap<Class<?>, TypeHandler>(_registry.getConfigurationAsMap(TYPE_HANDLERS, null)));
        }
        return _handlers.get();
    }

    @SuppressWarnings({ "unchecked" })
    public <T> T stringToObject( Module module, String input, Class<T> target, Location location )
    {
        Defense.notNull( target, "target" );
        if( input == null )
        {
            return null;
        }
        ConcurrentMap<Class<?>, TypeHandler> handlers = getHandlers();
        TypeHandler handler;
        handler = handlers.get( target );
        if( handler == null )
        {
            // the following sees if there is a public constructor with one string argument that can be used
            // we'll put the resulting converter into the map
            handler = StringConstructorHandler.tryCreate( target );
            if ( handler == null ) {
                handler = UNSUPPORTED;
            }
            // tryCreate() returns null, if there is no usable constructor, which we'll put into the map anyway
            // as sort of a cache: If null is in the handler map, we know we've already checked the type and
            // cannot convert the string
            TypeHandler prev = handlers.putIfAbsent( target, handler );
            if( prev != null )
            {
                handler = prev;
            }
        }
        assert handler != null;
        if( handler == UNSUPPORTED )
        {
            throw new ApplicationRuntimeException( ImplMessages.couldNotConvertStringToObject( input, target,
                    ImplMessages.noHandlerForType( target ) ),
                    location );
        }
        try
        {
            return (T) handler.stringToObject( module, input, location );
        }
        catch( ApplicationRuntimeException e )
        {
            throw e;
        }
        catch( Exception e )
        {
            throw new ApplicationRuntimeException( ImplMessages.couldNotConvertStringToObject( input, target, e.toString() ), location, e );
        }
    }

}
