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

import gaderian.test.services.ZapEvent;
import gaderian.test.services.ZapEventProducer;
import gaderian.test.services.ZapListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link gaderian.test.services.ZapEventProducer}.
 *
 * @author Howard Lewis Ship
 */
public class ZapEventProducerImpl implements ZapEventProducer
{
    private List _listeners = new ArrayList();

    public void addZapListener(ZapListener listener)
    {
        _listeners.add(listener);
    }

    public void removeZapListener(ZapListener listener)
    {
        _listeners.remove(listener);
    }

    public void fireZapDidWiggle(ZapEvent event)
    {
        int count = _listeners.size();
        for (int i = 0; i < count; i++)
        {
            ZapListener l = (ZapListener) _listeners.get(i);

            l.zapDidWiggle(event);
        }
    }

}
