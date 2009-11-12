package org.ops4j.gaderian.examples.chain.impl;

import org.ops4j.gaderian.examples.chain.AnimalClassifier;
import org.ops4j.gaderian.examples.chain.AnimalClassification;
import org.ops4j.gaderian.examples.chain.Animal;

/**
 * @author Johan Lindquist
 */
public class VertebratesAnimalClassifier implements AnimalClassifier
{
    public AnimalClassification classifiy( final Animal animal )
    {
        switch ( animal )
        {
            case COW:
            case EAGLE:
            case PINGVIN:
            case SHARK:
            case TUNA:
            case WOLF:
            case TURTLE:
                return AnimalClassification.VERTEBRATES;
            default:
                return null;
        }
    }
}