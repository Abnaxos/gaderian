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

package org.ops4j.gaderian.parse;

/**
 * Any Descriptor object which can be annotated with a description implements this interface.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public interface AnnotationHolder
{
    public String getAnnotation();

    public void setAnnotation(String annotation);
}