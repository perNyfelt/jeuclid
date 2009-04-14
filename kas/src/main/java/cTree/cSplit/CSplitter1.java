/*
 * Copyright 2009 Erhard Kuenzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cTree.cSplit;

import cTree.CElement;

public class CSplitter1 {

    public CElement split(final CElement parent, final CElement cE1,
            final String operator) {
        System.out.println("Do the Default split");
        return cE1;
    }

    private void init(final CElement cE1, final String operator) {
    }

    public boolean check(final CElement cE1, final String operator) {
        return false;
    }

}
