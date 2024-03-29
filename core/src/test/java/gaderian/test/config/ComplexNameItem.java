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

import org.ops4j.gaderian.impl.BaseLocatable;


public class ComplexNameItem extends BaseLocatable
{
	private String _complexAttributeName;
	
    public String getComplexAttributeName()
    {
        return _complexAttributeName;
    }

    public void setComplexAttributeName(String string)
    {
        _complexAttributeName = string;
    }

}
