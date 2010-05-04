package org.ops4j.gaderian.impl.types;

import java.io.File;

import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.TypeHandler;
import org.ops4j.gaderian.internal.Module;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class FileHandler implements TypeHandler {

    public Object stringToObject(Module module, String input, Location location) throws Exception {
        return new File(input.replace('/', File.separatorChar));
    }
}
