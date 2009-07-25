// Copyright 2007 The Apache Software Foundation
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

package org.ops4j.gaderian.annotations.registry;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ops4j.gaderian.annotations.registry.ServiceModel;
import org.ops4j.gaderian.annotations.registry.InvalidationPolicy;
import org.ops4j.gaderian.annotations.registry.Visibility;

/**
 * Marks a method in an annotated module as service point. 
 * The return type of the method defines the service interface. 
 * The method is used factory method for the construction of service implementations.
 * 
 * @author Achim Huegen
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Service {
    String id();
    Visibility visibility() default Visibility.PUBLIC;
    ServiceModel serviceModel() default ServiceModel.SINGLETON;
    InvalidationPolicy invalidationPolicy() default InvalidationPolicy.SHUTDOWN;
}
