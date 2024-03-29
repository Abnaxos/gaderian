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

package org.ops4j.gaderian.service;

/**
 * Service used when dynamically creating new classes.
 * 
 * @author Howard Lewis Ship
 */
public interface ClassFactory
{
    /**
     * Creates a {@link ClassFab} object for the given name; the new class is a subclass of the
     * indicated class. The new class is public and concrete.
     * <p>
     * <b>Incompatible change from 1.0: The module parameter has been removed. </b>
     * 
     * @param name
     *            the full qualified name of the class to create
     * @param superClass
     *            the parent class, which is often java.lang.Object
     */

    ClassFab newClass(String name, Class superClass);

    /**
     * Creates a new {@link org.ops4j.gaderian.service.InterfaceFab} object with the given name.
     * 
     * @since 1.1
     */

    InterfaceFab newInterface(String name);

    /**
     * Returns the number of classes (and interfaces) actually created.
     * 
     * @since 1.2
     */

    int getCreatedClassCount();
}