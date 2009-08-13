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

package org.ops4j.gaderian.impl;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.gaderian.ApplicationRuntimeException;
import org.ops4j.gaderian.ErrorHandler;
import org.ops4j.gaderian.Location;
import org.ops4j.gaderian.internal.RegistryInfrastructure;
import org.ops4j.gaderian.schema.Translator;
import org.ops4j.gaderian.schema.rules.ClassTranslator;
import org.ops4j.gaderian.schema.rules.InstanceTranslator;
import org.ops4j.gaderian.schema.rules.ServiceTranslator;
import org.ops4j.gaderian.schema.rules.SmartTranslator;

/**
 * Manages translators for {@link org.ops4j.gaderian.impl.RegistryInfrastructureImpl}.
 * 
 * @author Howard Lewis Ship
 */
public class TranslatorManager
{
    static final Log LOG = LogFactory.getLog(TranslatorManager.class);

    public static final String TRANSLATORS_CONFIGURATION_ID = "gaderian.Translators";

    private ErrorHandler _errorHandler;

    private RegistryInfrastructure _registry;

    /**
     * Map of Class, keyed on translator name, used to instantiate new
     * {@link org.ops4j.gaderian.schema.Translator}s. Loaded from the
     * <code>gaderian.Translators</code> configuration point;
     */
    private Map<String,Class<? extends Translator>> _translatorClasses = new HashMap<String,Class<? extends Translator>>();

    private Map<String,Translator> _translatorsCache = new HashMap<String,Translator>();

    private boolean _translatorsLoaded;

    public TranslatorManager(RegistryInfrastructure registry, ErrorHandler errorHandler)
    {
        _registry = registry;
        _errorHandler = errorHandler;

        // Seed the basic translators used to "bootstrap" the
        // processing of the gaderian.Translators configuration point.

        _translatorsCache.put("class", new ClassTranslator());
        _translatorsCache.put("service", new ServiceTranslator());
        _translatorsCache.put("smart", new SmartTranslator());
        _translatorsCache.put("instance", new InstanceTranslator());

        // smart may take an initializer, so we need to put it into the classes as
        // well.

        _translatorClasses.put("smart", SmartTranslator.class);

    }

    public synchronized Translator getTranslator(String constructor)
    {
        // The cache is preloaded with the hardcoded translators.

        if (!_translatorsLoaded && !_translatorsCache.containsKey(constructor))
            loadTranslators();

        Translator result = _translatorsCache.get(constructor);

        if (result == null)
        {
            result = constructTranslator(constructor);
            _translatorsCache.put(constructor, result);
        }

        return result;
    }

    private Translator constructTranslator(String constructor)
    {
        String name = constructor;
        String initializer = null;

        int commax = constructor.indexOf(',');

        if (commax > 0)
        {
            name = constructor.substring(0, commax);
            initializer = constructor.substring(commax + 1);
        }

        Class<? extends Translator> translatorClass = findTranslatorClass(name);

        // TODO: check for null class, meaning that the translator is a service.

        return createTranslator(translatorClass, initializer);
    }

    private Translator createTranslator(Class<? extends Translator> translatorClass, String initializer)
    {
        try
        {

            if (initializer == null)
                return translatorClass.newInstance();

            Constructor<? extends Translator> c = translatorClass.getConstructor(String.class);

            return c.newInstance(initializer );
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.translatorInstantiationFailure(
                    translatorClass,
                    ex), ex);
        }
    }

    private Class<? extends Translator> findTranslatorClass(String translatorName)
    {
        Class<? extends Translator> result = _translatorClasses.get(translatorName);

        if (result == null)
            throw new ApplicationRuntimeException(ImplMessages.unknownTranslatorName(
                    translatorName,
                    TRANSLATORS_CONFIGURATION_ID));

        return result;
    }

    private void loadTranslators()
    {
        // Prevent endless recursion!

        _translatorsLoaded = true;

        List<TranslatorContribution> contributions = _registry.getConfiguration(TRANSLATORS_CONFIGURATION_ID, null);

        Map<String, Location> locations = new HashMap<String, Location>();
        locations.put("class", null);

      for (final TranslatorContribution contribution : contributions)
      {

        String name = contribution.getName();
        Location oldLocation = locations.get(name);

        if (oldLocation != null)
        {
          _errorHandler.error(LOG, ImplMessages.duplicateTranslatorName(name, oldLocation), contribution
              .getLocation(), null);

          continue;
        }

        locations.put(name, contribution.getLocation());

        Translator t = contribution.getTranslator();

        if (t != null)
        {
          _translatorsCache.put(name, t);
          continue;
        }

        Class<? extends Translator> tClass = contribution.getTranslatorClass();

        if (tClass == null)
        {
          _errorHandler.error(
              LOG,
              ImplMessages.incompleteTranslator(contribution),
              contribution.getLocation(),
              null);
          continue;
        }

        _translatorClasses.put(name, tClass);
      }

    }

}