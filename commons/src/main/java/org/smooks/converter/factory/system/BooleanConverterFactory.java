/*-
 * ========================LICENSE_START=================================
 * Smooks Commons
 * %%
 * Copyright (C) 2020 Smooks
 * %%
 * Licensed under the terms of the Apache License Version 2.0, or
 * the GNU Lesser General Public License version 3.0 or later.
 * 
 * SPDX-License-Identifier: Apache-2.0 OR LGPL-3.0-or-later
 * 
 * ======================================================================
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ======================================================================
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * =========================LICENSE_END==================================
 */
package org.smooks.converter.factory.system;

import org.smooks.converter.TypeConverter;
import org.smooks.converter.TypeConverterDescriptor;
import org.smooks.converter.TypeConverterException;
import org.smooks.converter.factory.TypeConverterFactory;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Resource(name = "Boolean")
public class BooleanConverterFactory implements TypeConverterFactory<String, Boolean> {
    private static final List<String> TRUE_VALUES = Arrays.asList("y", "yes", "true", "1");
    private static final List<String> FALSE_VALUES = Arrays.asList("n", "no", "false", "0");

    @Override
    public TypeConverter<String, Boolean> createTypeConverter() {
        return value -> {
            if (TRUE_VALUES.contains(value.trim().toLowerCase())) {
                return Boolean.TRUE;
            } else if (FALSE_VALUES.contains(value.trim().toLowerCase())) {
                return Boolean.FALSE;
            }

            try {
                return Boolean.parseBoolean(value.trim());
            } catch (NumberFormatException e) {
                throw new TypeConverterException("Failed to decode Boolean value '" + value + "'.", e);
            }
        };
    }

    @Override
    public TypeConverterDescriptor<Class<String>, Class<Boolean>> getTypeConverterDescriptor() {
        return new TypeConverterDescriptor<>(String.class, Boolean.class);
    }
}