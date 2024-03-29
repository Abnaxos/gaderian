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

package org.ops4j.gaderian.schema;

import org.ops4j.gaderian.Element;
import org.ops4j.gaderian.Locatable;

/**
 * Rules associated with {@link org.ops4j.gaderian.schema.Schema} elements.
 * As the {@link org.ops4j.gaderian.schema.SchemaProcessor}
 * works through the {@link org.ops4j.gaderian.Element} hiearchy, it invokes
 * methods on the Rules.
 * 
 * @author Howard M. Lewis Ship
 */
public interface Rule extends Locatable
{
	/**
	 * Begin rules are fired first, in order.  All rules are invoked, and then the
	 * SchemaProcessor recurses into the elements contained within the element.
	 */
    public void begin(SchemaProcessor processor, Element element);

	/**
	 * End rules are fired last, in inverse order.
	 */
    public void end(SchemaProcessor processor, Element element);
}
