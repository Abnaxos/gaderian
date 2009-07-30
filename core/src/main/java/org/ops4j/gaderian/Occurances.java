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

package org.ops4j.gaderian;

/**
 * Identifies the number of contributions allowed to a configuration extension point.
 * 
 * @author Howard Lewis Ship
 */
public enum Occurances
{
    /**
     * An unbounded number, zero or more.
     */
    UNBOUNDED(new RangeChecker() {

        public boolean inRange(final int count)
        {
            return true;
        }
    }),

    /**
     * Optional, may be zero or one, but not more
     */
    OPTIONAL(new RangeChecker() {

        public boolean inRange(final int count)
        {
            return count < 2;
        }
    }),
    /**
     * Exactly one is required.
     */
    REQUIRED(new RangeChecker() {

        public boolean inRange(final int count)
        {
            return count == 1;
        }
    }),

    /**
     * At least one is required.
     */
    ONE_PLUS(new RangeChecker() {

        public boolean inRange(final int count)
        {
            return count > 0;
        }
    }),

    /**
     * No
     */
    NONE(new RangeChecker() {

        public boolean inRange(final int count)
        {
            return count == 0;
        }
    });

    private Occurances.RangeChecker _rangeChecker;

    Occurances(final RangeChecker rangeChecker)
    {
        _rangeChecker = rangeChecker;
    }

    /**
     * Validates that an actual count is in range for the particular Occurances count.
     *
     * @param count
     *            the number of items to check. Should be zero or greater.
     * @return true if count is a valid number in accordance to the range, false otherwise
     */
    public boolean inRange(int count)
    {
        return _rangeChecker.inRange(count);
    }

    /** Defines the interface for internal range checking within the enum.
     */
    private static interface RangeChecker
    {
        /**
         * Validates that an actual count is in range for the particular Occurances count.
         *
         * @param count
         *            the number of items to check. Should be zero or greater.
         * @return true if count is a valid number in accordance to the range, false otherwise
         */
        public boolean inRange(int count);
    }

}