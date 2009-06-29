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

package gaderian.test.services.impl;

import gaderian.test.services.ErrorHandlerHolder;

import org.ops4j.gaderian.ErrorHandler;

public class ErrorHandlerHolderImpl implements ErrorHandlerHolder
{
    public ErrorHandlerHolderImpl()
    {
    }

    public ErrorHandlerHolderImpl(ErrorHandler errorHandler)
    {
        _errorHandler = errorHandler;
    }

    private ErrorHandler _errorHandler;

    public ErrorHandler getErrorHandler()
    {
        return _errorHandler;
    }

    public void setErrorHandler(ErrorHandler handler)
    {
        _errorHandler = handler;
    }

}
