package org.ops4j.gaderian.examples.chain;

/**
 * @author Johan Lindquist
 */
// START SNIPPET:full
public interface AnimalClassifier
{
    /** Classifies the specified <code>Animal</code>.
     * @param animal The animal to classify
     * @return The classification of the specified animal
     */
    AnimalClassification classify(Animal animal);
}
// END SNIPPET:full
