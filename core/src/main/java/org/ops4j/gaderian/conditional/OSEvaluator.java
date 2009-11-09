package org.ops4j.gaderian.conditional;

/**
 * Evaluator for the current operating system.
 *
 *
 * @author Johan Lindquist
 * @since Gaderian 1.1
 */
public class OSEvaluator implements Evaluator
{
    private OperatingSystem _operatingSystem;

    public OSEvaluator( final String operatingSystem )
    {
        _operatingSystem = OperatingSystem.valueOf( operatingSystem );
    }

    OperatingSystem getOperatingSystem()
    {
        return _operatingSystem;
    }

    public boolean evaluate( final EvaluationContext evaluationContext, final Node node )
    {
        final OperatingSystem currentOperatingSystem = getOperatingSystemType(evaluationContext);
        return _operatingSystem == currentOperatingSystem;
    }

    private OperatingSystem getOperatingSystemType(final EvaluationContext evaluationContext)
    {
        // Retrieve the current operating system type from the context
        String currentOperatingSystem = evaluationContext.getOperatingSystemProperty( "generic" );

        if ( currentOperatingSystem.startsWith( "windows" ) )
        {
            return OperatingSystem.WINDOWS;
        }
        else if ( currentOperatingSystem.startsWith( "linux" ) )
        {
            return OperatingSystem.LINUX;
        }
        else if ( currentOperatingSystem.startsWith( "sunos" ) )
        {
            return OperatingSystem.SUNOS;
        }
        else if ( currentOperatingSystem.startsWith( "mac" ) || currentOperatingSystem.startsWith( "darwin" ) )
        {
            return OperatingSystem.MAC;
        }
        else if ( currentOperatingSystem.startsWith( "aix" ) )
        {
            return OperatingSystem.AIX;
        }
        else
        {
            return OperatingSystem.GENERIC;
        }
    }


}
