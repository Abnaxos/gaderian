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

import org.ops4j.gaderian.Resource;
import org.ops4j.gaderian.impl.BaseLocatable;
import org.ops4j.gaderian.util.ToStringBuilder;

/**
 * Descriptor for &lt;sub-module&gt; element.
 * 
 * @author Knut Wannheden
 */
public final class SubModuleDescriptor extends BaseLocatable
{

    private Resource _descriptor;

    private String _conditionalExpression;

    public Resource getDescriptor()
    {
        return _descriptor;
    }

    public void setDescriptor(Resource descriptor)
    {
        _descriptor = descriptor;
    }

    /**
     * @since 1.1.2
     */
    public String getConditionalExpression()
    {
        return _conditionalExpression;
    }

    /**
     * @since 1.1.2
     */
    public void setConditionalExpression(String conditionalExpression)
    {
        _conditionalExpression = conditionalExpression;
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("descriptor", _descriptor);

        return builder.toString();
    }

}
