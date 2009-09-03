package gaderian.test.services.impl;

import java.util.List;

import org.ops4j.gaderian.service.ThreadLocale;
import org.ops4j.gaderian.annotations.validation.Required;
import gaderian.test.services.RequiredAnnotation;

/**
 * @author Johan Lindquist
 */
public class RequiredAnnotationImpl implements RequiredAnnotation
{
    @Required
    private List requiredList;

    public void doIt()
    {
        // no op
    }
}
