package org.ops4j.gaderian.examples.chain;

import org.ops4j.gaderian.examples.ExampleUtils;
import org.ops4j.gaderian.Registry;
import org.ops4j.gaderian.testutils.GaderianTestCase;

/**
 * @author Johan Lindquist
 */
public class TestChain extends GaderianTestCase
{
    public AnimalClassifier _animalClassifier;

    @Override
    protected void setUp() throws Exception
    {
        Registry registry = ExampleUtils.buildClasspathRegistry( "/org/ops4j/gaderian/examples/chain/chain.xml" );
        _animalClassifier = registry.getService( AnimalClassifier.class );
    }

    public void testFishClassification() throws Exception
    {
        AnimalClassification animalClassification = _animalClassifier.classify( Animal.SHARK );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.FISH, animalClassification );

        animalClassification = _animalClassifier.classify( Animal.TUNA );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.FISH, animalClassification );

    }

    public void testBirdsClassification() throws Exception
    {
        AnimalClassification animalClassification = _animalClassifier.classify( Animal.PINGVIN );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.BIRDS, animalClassification );

        animalClassification = _animalClassifier.classify( Animal.EAGLE );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.BIRDS, animalClassification );

    }

    public void testMammalsClassification() throws Exception
    {
        AnimalClassification animalClassification = _animalClassifier.classify( Animal.WOLF );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.MAMMALS, animalClassification );

        animalClassification = _animalClassifier.classify( Animal.COW );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.MAMMALS, animalClassification );

    }

     public void testInsectsClassification() throws Exception
    {
        AnimalClassification animalClassification = _animalClassifier.classify( Animal.WOLF );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.MAMMALS, animalClassification );

        animalClassification = _animalClassifier.classify( Animal.COW );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.MAMMALS, animalClassification );

    }

    public void testInvertebratesClassification() throws Exception
    {
        AnimalClassification animalClassification = _animalClassifier.classify( Animal.SPIDER );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.INVERTEBRATES , animalClassification );

    }


    public void testVertebratesClassification() throws Exception
    {
        AnimalClassification animalClassification = _animalClassifier.classify( Animal.TURTLE );
        assertNotNull( animalClassification );
        assertEquals( AnimalClassification.VERTEBRATES, animalClassification );

    }


}