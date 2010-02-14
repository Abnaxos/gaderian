package org.ops4j.gaderian.util;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.ops4j.gaderian.testutils.GaderianTestCase;

/**
 * @author Johan Lindquist
 */
public class TestURLResource extends GaderianTestCase
{
    private URL _url1;
    private URL _url2;
    private URL _url2Same;

    public void testHashCodeEquals()
    {

        URLResource urlResource1 = new URLResource( _url1 );
        URLResource urlResource2 = new URLResource( _url2 );
        URLResource urlResource2Same = new URLResource( _url2Same );

        assertTrue("bad equals", urlResource1.equals( urlResource1));
        assertEquals("bad hash code", urlResource1.hashCode(), urlResource1.hashCode());
        assertFalse("bad equals", urlResource1.equals( urlResource2));
        assertNotSame("bad hash code", urlResource1.hashCode(), urlResource2.hashCode());
        assertTrue("bad equals", urlResource2.equals( urlResource2Same));
        assertEquals("bad hash code", urlResource2.hashCode(), urlResource2Same.hashCode());

        Set<URLResource> urlResourceSet = new HashSet<URLResource>( );
        assertTrue(urlResourceSet.add( urlResource1 ));
        assertFalse(urlResourceSet.add( urlResource1 ));
        assertTrue(urlResourceSet.add( urlResource2 ));
        assertFalse(urlResourceSet.add( urlResource2Same ));

    }

    protected void setUp() throws Exception
    {
        super.setUp();
        _url1 = new URL( "http://one");
        _url2 = new URL( "http://two");
        _url2Same = new URL( "http://two");
    }
}
