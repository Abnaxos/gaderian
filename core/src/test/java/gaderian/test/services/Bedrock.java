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

package gaderian.test.services;

/**
 * Interface used to test {@link gaderian.test.services.impl.FilterLoggingInterceptor},
 * which itself tests the ability to pass parameters to a interceptor factory.
 *
 * @author Howard Lewis Ship
 */
public interface Bedrock
{
	public void fred();
	
	public void barney();
	
	public void wilma();
}
