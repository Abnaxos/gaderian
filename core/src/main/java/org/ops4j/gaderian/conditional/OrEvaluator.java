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
 * Or operation, returns true if either left or right Node evaluates to true.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class OrEvaluator implements Evaluator
{

    public boolean evaluate(EvaluationContext context, Node node)
    {
        return node.getLeft().evaluate(context) || node.getRight().evaluate(context);
    }

}