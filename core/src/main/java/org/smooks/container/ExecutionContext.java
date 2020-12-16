/*-
 * ========================LICENSE_START=================================
 * Smooks Core
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
package org.smooks.container;

import org.smooks.delivery.ContentDeliveryConfig;
import org.smooks.event.ExecutionEventListener;
import org.smooks.javabean.context.BeanContext;
import org.smooks.profile.ProfileSet;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.Writer;
import java.net.URI;

/**
 * Runtime context of a filter execution.
 */
@NotThreadSafe
public interface ExecutionContext extends TypedMap {
    
    /**
     * Sometimes the document being transformed/analysed has a URI associated with it.
     * This can be bound to the execution context under this key.
     */
    URI DOCUMENT_URI = URI.create("org:smooks:unknowndoc");

    /**
     * Sets the document source URI of this <code>ExecutionContext</code>.
     *
     * @param docSource  the document URI
     */
    void setDocumentSource(URI docSource);

    /**
     * Gets the document source URI of this <code>ExecutionContext</code>.
     * <p/>
     * If the document source URI is not set for the context, implementations should
     * return the {@link #DOCUMENT_URI} constant.
     *
     * @return  the document URI.
     */
    URI getDocumentSource();

    /**
     * Get the {@link ApplicationContext} of this <code>ExecutionContext</code>.
     *
     * @return  the <code>ApplicationContext</code> instance.
     */
    ApplicationContext getApplicationContext();

    /**
     * Get the set of profiles at which this execution context is targeted.
     * <p/>
     * Basically, the set of profiles for which this execution context is to perform
     * transformation/analysis.
     *
     * @return  the target {@link org.smooks.profile.ProfileSet}.
     */
    ProfileSet getTargetProfiles();

    /**
     * Get the content delivery configuration for the profile set at which this
     * context is targeted.
     *
     * @return  ContentDeliveryConfig instance.
     */
	ContentDeliveryConfig getDeliveryConfig();


    /**
     * Set the content encoding to be used when parsing content on this context.
     * @param  contentEncoding Character encoding to be used when parsing content. Null defaults to "UTF-8".
     * @throws IllegalArgumentException Invalid encoding.
     */
    void setContentEncoding(String contentEncoding) throws IllegalArgumentException;

    /**
     * Get the content encoding to be used when parsing content on this context.
     * 
     * @return Character encoding to be used when parsing content.  Defaults to "UTF-8".
     */
    String getContentEncoding();

    /**
     * Set the ExecutionEventListener for the {@link ExecutionContext}.
     * <p/>
     * Allows calling code to listen to (and capture data on) specific
     * context execution events e.g. the targeting of resources.
     * <p/>
     * Note, this is not a logging facility and should be used with care.
     * It's overuse should be avoided as it can have a serious negative effect
     * on performance.  By default, no listenrs are applied and so no overhead
     * is incured.
     *
     * @param listener The listener instance.
     * @see org.smooks.event.BasicExecutionEventListener
     */
    void setEventListener(ExecutionEventListener listener);

    /**
     * Get the ExecutionEventListener for the {@link ExecutionContext}.
     * @return The listener instance.
     * @see #setEventListener(ExecutionEventListener)
     */
    ExecutionEventListener getEventListener();

    /**
     * Set the error/exception that caused the filter operation associated with
     * this ExecutionContext to terminate.
     *
     * @param terminationError The termination Error/Exception.
     */
    void  setTerminationError(Throwable terminationError);


    /**
     * Set the error/exception that caused the filter operation associated with
     * this ExecutionContext to terminate.
     *
     * @return The Error/Exception that caused the associated filter operation to
     * terminate (if it did terminate), otherwise null.
     */
    Throwable getTerminationError();

    /**
     * Get a global configuration parameter associated with this execution context.
     * <p/>
     * For more fine grained control, see the {@link org.smooks.cdr.ParameterAccessor} class.
     * @param name The name of the parameter.
     * @return The parameter value, or null if the parameter is not configured.
     */
    String getConfigParameter(String name);

    /**
     * Get a global configuration parameter associated with this execution context.
     * <p/>
     * For more fine grained control, see the {@link org.smooks.cdr.ParameterAccessor} class.
     * @param name The name of the parameter.
     * @param defaultVal The default value to be returned if the configuration parameter is not set.
     * @return The parameter value, or "defaultVal" if the parameter is not configured.
     */
    String getConfigParameter(String name, String defaultVal);

    /**
     * Gets the {@link BeanContext} for this <code>ExecutionContext</code>.
     * 
     * @return  the <code>BeanContext</code>
     */
    BeanContext getBeanContext();

    /**
     * Sets the {@link BeanContext} for this <code>ExecutionContext</code>.
     * 
     * @param beanContext  the <code>BeanContext</code>
     */
    void setBeanContext(BeanContext beanContext);

    /**
     * Sets output stream <code>Writer</code> for this <code>ExecutionContext</code>. Typically it is the <code>Filter</code> 
     * that sets the <code>Writer</code>. Resources like <code>Visitor</code>s should leave this method alone.
     * 
     * @param writer  the output stream <code>Writer</code>
     */
    void setWriter(Writer writer);

    /**
     * @return  the output stream <code>Writer</code> for this <code>ExecutionContext</code>
     */
    Writer getWriter();

    /**
     * @return  the {@link MementoCaretaker} for this <code>ExecutionContext</code>
     */
    MementoCaretaker getMementoCaretaker();
}
