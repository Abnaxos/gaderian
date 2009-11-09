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

package org.ops4j.gaderian.conditional;

/**
 * Provides context when evaluating an AST of {@link org.ops4j.gaderian.conditional.Node}s.
 * Effectively, a wrapper around certain runtime operations.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public interface EvaluationContext
{
    /** Returns the string of the currently running operating system.
     *
     * @param defaultValue The default value to use if the operating system can not be determined
     * @return The
     * @since Gaderian 1.1
     */
    public String getOperatingSystemProperty(String defaultValue);
    /**
     * Returns true if the given system property is set.
     */
    public boolean isPropertySet(String propertyName);
    
    /**
     * Returns true if the class, specified by FQCN, exists.
     * 
     */
    public boolean doesClassExist(String className);
}