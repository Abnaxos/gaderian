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

package gaderian.test.config;

import org.ops4j.gaderian.Element;
import org.ops4j.gaderian.schema.SchemaProcessor;
import org.ops4j.gaderian.schema.rules.BaseRule;
import org.ops4j.gaderian.schema.rules.RuleUtils;

/**
 * Tests class that's actually kind of useful; sets a "module"
 * property of the top object to be the contributing module.
 *
 * @author Howard Lewis Ship
 */
public class SetContributingModuleRule extends BaseRule
{

    public void begin(SchemaProcessor processor, Element element)
    {
        RuleUtils.setProperty(
            processor,
            element,
            "contributingModule",
            processor.peek(),
            processor.getContributingModule());
    }

}
