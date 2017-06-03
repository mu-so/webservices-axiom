/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axiom.om.impl.common.util;

import java.util.Locale;

public final class LocaleUtil {
    private LocaleUtil() {}

    public static int getMatchScore(Locale requested, Locale candidate) {
        if (candidate == null) {
            return 1;
        } else if (requested.getLanguage().equals(candidate.getLanguage())) {
            if (requested.getCountry().equals(candidate.getCountry())) {
                return 6;
            } else if (candidate.getCountry().isEmpty()) {
                return 5;
            } else {
                return 4;
            }
        } else if (candidate.getLanguage().equals("en")) {
            return candidate.getCountry().isEmpty() ? 3 : 2;
        } else {
            return 0;
        }
    }
}
