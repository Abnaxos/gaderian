// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.ops4j.gaderian.utilities.strategy;

import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.easymock.Capture;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.ops4j.gaderian.ErrorLog;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.ServiceImplementationFactoryParameters;
import org.ops4j.gaderian.service.*;
import org.ops4j.gaderian.test.GaderianCoreTestCase;
import org.ops4j.gaderian.utilities.util.StrategyRegistry;

/**
 * Test for the {@link org.ops4j.gaderian.utilities.strategy.StrategyFactory} service
 * implementation factory.
 *
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class TestStrategyFactory extends GaderianCoreTestCase
{
    private List buildContributions( Class registerClass, Object adapter, Location location )
    {
        StrategyContribution c = new StrategyContribution();

        c.setRegisterClass( registerClass );
        c.setStrategy( adapter );
        c.setLocation( location );

        return Collections.singletonList( c );
    }

    private StrategyParameter buildParameter( Class registerClass, Object adapter,
                                              Location contributionLocation, Location parameterLocation )
    {
        StrategyParameter result = new StrategyParameter();

        result.setContributions( buildContributions( registerClass, adapter, contributionLocation ) );
        result.setLocation( parameterLocation );

        return result;
    }

    private StrategyParameter buildParameter( Class registerClass, Object adapter )
    {
        return buildParameter( registerClass, adapter, null, null );
    }

    public void testBuildRegistry()
    {
        StrategyRegistry ar = createMock( StrategyRegistry.class );
        ToStringStrategy adapter = createMock( ToStringStrategy.class );

        ServiceImplementationFactoryParameters fp = createMock( ServiceImplementationFactoryParameters.class );

        expect( fp.getServiceInterface() ).andReturn( ToStringStrategy.class );

        StrategyParameter p = buildParameter( Number.class, adapter );

        expect( fp.getFirstParameter() ).andReturn( p );

        ar.register( Number.class, adapter );

        replayAllRegisteredMocks();

        new StrategyFactory().buildRegistry( fp, ar );

        verifyAllRegisteredMocks();
    }

    public void testBuildRegistryWrongAdapterType()
    {
        Location l = newLocation();

        StrategyRegistry ar = createMock( StrategyRegistry.class );
        ToStringStrategy adapter = createMock( ToStringStrategy.class );

        ServiceImplementationFactoryParameters fp = createMock( ServiceImplementationFactoryParameters.class );

        ErrorLog log = createMock( ErrorLog.class );

        expect( fp.getServiceInterface() ).andReturn( Runnable.class );

        StrategyParameter p = buildParameter( Number.class, adapter, l, null );

        expect( fp.getFirstParameter() ).andReturn( p );

        expect( fp.getErrorLog() ).andReturn( log );


        log.error( eq( StrategyMessages.strategyWrongInterface( adapter, Number.class, Runnable.class ) ),
                eq( l ),
                EasyMock.isA( ClassCastException.class ) );

        replayAllRegisteredMocks();

        new StrategyFactory().buildRegistry( fp, ar );

        verifyAllRegisteredMocks();
    }

    public void testBuildImplementationClass()
    {
        ClassFactory factory = createMock( ClassFactory.class );

        ClassFab cf = createMock( ClassFab.class );

        MethodFab mf = createMock( MethodFab.class );

        ServiceImplementationFactoryParameters fp = createMock( ServiceImplementationFactoryParameters.class );

        expect( fp.getServiceInterface() ).andReturn( ToStringStrategy.class );
        final StrategyParameter param = new StrategyParameter();
        expect( fp.getFirstParameter() ).andReturn( param );
        expect( factory.newClass( "NewClass", Object.class ) ).andReturn( cf );

        cf.addInterface( ToStringStrategy.class );
        cf.addField( "_registry", StrategyRegistry.class );

        Capture<Class[]> classCapture = new Capture<Class[]>();
        cf.addConstructor( capture( classCapture ), ( Class[] ) eq(null), eq("_registry = $1;") );

        expect( cf.addMethod(
                Modifier.PRIVATE,
                new MethodSignature( ToStringStrategy.class, "_getStrategy", new Class[]
                        { Object.class }, null ),
                "return (org.ops4j.gaderian.utilities.strategy.ToStringStrategy) _registry.getStrategy($1.getClass());" ) ).andReturn( mf );

        expect( cf.addMethod( Modifier.PUBLIC, new MethodSignature( String.class, "toString", new Class[]
                { Object.class }, null ), "return ($r) _getStrategy($1).toString($$);" ) ).andReturn( mf );

        expect( fp.getServiceId() ).andReturn( "foo.Bar" );

        ClassFabUtils.addToStringMethod( cf, StrategyMessages.toString(
                "foo.Bar",
                ToStringStrategy.class ) );
        expectLastCall().andReturn( mf );

        expect( cf.createClass() ).andReturn( String.class );

        replayAllRegisteredMocks();

        StrategyFactory f = new StrategyFactory();
        f.setClassFactory( factory );

        f.buildImplementationClass( fp, "NewClass" );

        verifyAllRegisteredMocks();

        Class[] value = classCapture.getValue();
        assertNotNull(value);
        assertEquals("unexpected constructor length", 1,value.length);

    }

    public void testBuildImplementationClassImproperMethod()
    {
        Location l = newLocation();

        ClassFactory factory = createMock( ClassFactory.class );

        ClassFab cf = createMock( ClassFab.class );

        MethodFab mf = createMock( MethodFab.class );

        ServiceImplementationFactoryParameters fp = createMock( ServiceImplementationFactoryParameters.class );

        ErrorLog log = createMock( ErrorLog.class );

        expect(fp.getServiceInterface()).andReturn( Runnable.class );
        final StrategyParameter param = new StrategyParameter();
        expect(fp.getFirstParameter()).andReturn( param );

        expect(factory.newClass( "NewClass", Object.class )).andReturn( cf );

        cf.addInterface( Runnable.class );
        cf.addField( "_registry", StrategyRegistry.class );

        Capture<Class[]> classCapture = new Capture<Class[]>();
        cf.addConstructor( capture( classCapture ), ( Class[] ) eq(null), eq("_registry = $1;") );


        expect(cf.addMethod(
                Modifier.PRIVATE,
                new MethodSignature( Runnable.class, "_getStrategy", new Class[]
                        { Object.class }, null ),
                "return (java.lang.Runnable) _registry.getStrategy($1.getClass());" )).andReturn( mf );

        MethodSignature sig = new MethodSignature( void.class, "run", null, null );

        expect(cf.addMethod( Modifier.PUBLIC, sig, "{  }" )).andReturn( mf );

        expect(fp.getErrorLog()).andReturn( log );

        // Slight fudge: we return the location itself when we should return
        // an object with this location.
        expect(fp.getFirstParameter()).andReturn( l );

        log.error( StrategyMessages.improperServiceMethod( sig ), l, null );

        expect(fp.getServiceId()).andReturn( "foo.Bar" );

        ClassFabUtils.addToStringMethod( cf, StrategyMessages.toString( "foo.Bar", Runnable.class ) );
        expectLastCall().andReturn( mf );
                              
        expect(cf.createClass()).andReturn( String.class );

        replayAllRegisteredMocks();

        StrategyFactory f = new StrategyFactory();
        f.setClassFactory( factory );

        f.buildImplementationClass( fp, "NewClass" );

        verifyAllRegisteredMocks();
    }

    public void testIntegration() throws Exception
    {
        Registry r = buildFrameworkRegistry( "AdapterFactoryIntegration.xml", false );

        ToStringStrategy ts = ( ToStringStrategy ) r.getService( ToStringStrategy.class );

        assertEquals( "5150", ts.toString( new Integer( 5150 ) ) );
    }

    public void testParameterIndex() throws Exception
    {
        Registry r = buildFrameworkRegistry( "ParameterIndexTest.xml", false );
        LoggingStrategy loggingStrategy = ( LoggingStrategy ) r.getService( LoggingStrategy.class );
        Log log = createMock( Log.class );
        final Date now = new Date();
        log.debug( "Hello, World!" );
        log.debug( MessageFormat.format( "{0,date,MM/dd/yyyy}", new Object[]{ now } ) );
        replayAllRegisteredMocks();
        loggingStrategy.log( log, "Hello, World!" );
        loggingStrategy.log( log, now );

        verifyAllRegisteredMocks();
    }
}