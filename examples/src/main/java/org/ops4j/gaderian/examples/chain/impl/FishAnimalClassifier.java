package org.ops4j.gaderian.examples.chain.impl;

import org.ops4j.gaderian.examples.chain.AnimalClassifier;
import org.ops4j.gaderian.examples.chain.AnimalClassification;
import org.ops4j.gaderian.examples.chain.Animal;

/**
 * @author Johan Lindquist
 */
public class FishAnimalClassifier implements AnimalClassifier
{
    public AnimalClassification classifiy( final Animal animal )
    {
        switch ( animal )
        {
            case SHARK:
            case TUNA:
                return AnimalClassification.FISH;
            default:
                return null;
        }
    }
}