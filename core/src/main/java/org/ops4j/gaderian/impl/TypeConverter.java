package org.ops4j.gaderian.impl;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.Module;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface TypeConverter
{
    @SuppressWarnings({ "unchecked" })
    <T> T stringToObject( Module module, String input, Class<T> target, Location location);
}
