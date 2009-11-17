package org.ops4j.gaderian.examples.chain.impl;

import org.ops4j.gaderian.examples.chain.AnimalClassifier;
import org.ops4j.gaderian.examples.chain.AnimalClassification;
import org.ops4j.gaderian.examples.chain.Animal;

/**
 * @author Johan Lindquist
 */
public class InvertebratesAnimalClassifier implements AnimalClassifier
{
    public AnimalClassification classify( final Animal animal )
    {
        switch ( animal )
        {
            case BEE:
            case LADYBIRD:
            case SPIDER:
                return AnimalClassification.INVERTEBRATES;
            default:
                return null;
        }
    }
}
