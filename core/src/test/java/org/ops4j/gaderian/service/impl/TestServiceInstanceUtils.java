package org.ops4j.gaderian.service.impl;

import junit.framework.TestCase;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.test.GaderianTestCase;

/**
 * @author Johan Lindquist
 */
public class TestServiceInstanceUtils extends GaderianTestCase
{
    public void testNoAnnotationsNoValues()
    {
        ServiceInstanceUtils.validate( NotAnnotatedTestClass.class, new NotAnnotatedTestClass() );
    }

    public void testNoAnnotationsWithValues()
    {
        final NotAnnotatedTestClass annotatedTestClass = new NotAnnotatedTestClass();
        annotatedTestClass.setNonRequiredField( "NotRequired" );
        ServiceInstanceUtils.validate( NotAnnotatedTestClass.class, annotatedTestClass );
    }

    public void testAnnotationsWithRequiredButNoNonRequired()
    {
        final AnnotatedTestClass annotatedTestClass = new AnnotatedTestClass();
        annotatedTestClass.setRequiredField( "Required" );
        ServiceInstanceUtils.validate( NotAnnotatedTestClass.class, annotatedTestClass );
    }

    public void testAnnotationsWithoutRequired()
    {
        final AnnotatedTestClass annotatedTestClass = new AnnotatedTestClass();
        try
        {
            ServiceInstanceUtils.validate( NotAnnotatedTestClass.class, annotatedTestClass );
            unreachable();
        }
        catch ( ApplicationRuntimeException e )
        {
            assertEquals("unexpected message", "Required field '_requiredField' of service instance 'org.ops4j.gaderian.service.impl.NotAnnotatedTestClass' is null", e.getMessage());
        }
    }

    public void testAnnotationsWithoutRequiredButWithNonRequired()
    {
        final AnnotatedTestClass annotatedTestClass = new AnnotatedTestClass();
        annotatedTestClass.setNonRequiredField( "NonRequired" );
        try
        {
            ServiceInstanceUtils.validate( NotAnnotatedTestClass.class, annotatedTestClass );
            unreachable();
        }
        catch ( ApplicationRuntimeException e )
        {
            assertEquals("unexpected message", "Required field '_requiredField' of service instance 'org.ops4j.gaderian.service.impl.NotAnnotatedTestClass' is null", e.getMessage());
        }
    }

}
