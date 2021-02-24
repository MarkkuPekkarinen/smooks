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
package org.smooks.engine.delivery.interceptor;

import org.smooks.api.SmooksException;
import org.smooks.api.ExecutionContext;
import org.smooks.api.delivery.sax.SAXElement;
import org.smooks.api.delivery.sax.SAXText;
import org.smooks.api.resource.visitor.sax.SAXElementVisitor;
import org.smooks.api.resource.visitor.sax.SAXVisitAfter;
import org.smooks.api.resource.visitor.sax.SAXVisitBefore;
import org.smooks.api.resource.visitor.sax.SAXVisitChildren;
import org.smooks.engine.delivery.event.VisitSequence;
import org.smooks.api.delivery.fragment.Fragment;
import org.smooks.engine.delivery.fragment.NodeFragment;
import org.smooks.engine.delivery.fragment.SAXElementFragment;
import org.smooks.api.resource.visitor.sax.ng.AfterVisitor;
import org.smooks.api.resource.visitor.sax.ng.BeforeVisitor;
import org.smooks.api.resource.visitor.sax.ng.ChildrenVisitor;
import org.smooks.api.resource.visitor.sax.ng.ElementVisitor;
import org.smooks.api.delivery.event.ExecutionEventListener;
import org.smooks.engine.delivery.event.ResourceTargetingEvent;
import org.smooks.engine.delivery.event.VisitEvent;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;

import java.io.IOException;

public class EventInterceptor extends AbstractInterceptorVisitor implements SAXElementVisitor, ElementVisitor {

    @Override
    public void visitBefore(SAXElement element, ExecutionContext executionContext) throws IOException {
        final Invocation<SAXVisitBefore> invocation = new Invocation<SAXVisitBefore>() {
            @Override
            public Object invoke(SAXVisitBefore visitor) {
                try {
                    visitor.visitBefore(element, executionContext);
                } catch (IOException e) {
                    throw new SmooksException(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Class<SAXVisitBefore> getTarget() {
                return SAXVisitBefore.class;
            }
        };
        if (getTarget() instanceof SAXVisitBefore) {
            final Fragment saxElementFragment = new SAXElementFragment(element);
            for (ExecutionEventListener executionEventListener : executionContext.getContentDeliveryRuntime().getExecutionEventListeners()) {
                executionEventListener.onEvent(new ResourceTargetingEvent(saxElementFragment, getTarget().getResourceConfig(), VisitSequence.BEFORE));
            }
            intercept(invocation);
            onEvent(executionContext, saxElementFragment, VisitSequence.BEFORE);
        } else {
            intercept(invocation);
        }
    }
    
    @Override
    public void visitAfter(SAXElement element, ExecutionContext executionContext) throws IOException {
        intercept(new Invocation<SAXVisitAfter>() {
            @Override
            public Object invoke(SAXVisitAfter visitor) {
                try {
                    visitor.visitAfter(element, executionContext);
                } catch (IOException e) {
                    throw new SmooksException(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Class<SAXVisitAfter> getTarget() {
                return SAXVisitAfter.class;
            }
        });
        
        if (getTarget() instanceof SAXVisitAfter) {
            onEvent(executionContext, new SAXElementFragment(element), VisitSequence.AFTER);
        }
    }
    
    @Override
    public void onChildText(SAXElement element, SAXText childText, ExecutionContext executionContext) throws IOException {
        intercept(new Invocation<SAXVisitChildren>() {
            @Override
            public Object invoke(SAXVisitChildren visitor) {
                try {
                    visitor.onChildText(element, childText, executionContext);
                } catch (IOException e) {
                    throw new SmooksException(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Class<SAXVisitChildren> getTarget() {
                return SAXVisitChildren.class;
            }
        });

        if (getTarget() instanceof SAXVisitChildren) {
            onEvent(executionContext, new SAXElementFragment(element), VisitSequence.AFTER);
        }
    }

    @Override
    public void onChildElement(SAXElement element, SAXElement childElement, ExecutionContext executionContext) throws IOException {
        intercept(new Invocation<SAXVisitChildren>() {
            @Override
            public Object invoke(SAXVisitChildren visitor) {
                try {
                    visitor.onChildElement(element, childElement, executionContext);
                } catch (IOException e) {
                    throw new SmooksException(e.getMessage(), e);
                }
                return null;
            }

            @Override
            public Class<SAXVisitChildren> getTarget() {
                return SAXVisitChildren.class;
            }
        });
        
        if (getTarget() instanceof SAXVisitChildren) {
            onEvent(executionContext, new SAXElementFragment(element), VisitSequence.AFTER);
        }
    }

    @Override
    public void visitBefore(Element element, ExecutionContext executionContext) {
        final Invocation<BeforeVisitor> invocation = new Invocation<BeforeVisitor>() {
            @Override
            public Object invoke(BeforeVisitor visitor) {
                visitor.visitBefore(element, executionContext);
                return null;
            }

            @Override
            public Class<BeforeVisitor> getTarget() {
                return BeforeVisitor.class;
            }
        };

        if (getTarget() instanceof BeforeVisitor) {
            for (ExecutionEventListener executionEventListener : executionContext.getContentDeliveryRuntime().getExecutionEventListeners()) {
                executionEventListener.onEvent(new ResourceTargetingEvent(new NodeFragment(element), getTarget().getResourceConfig(), VisitSequence.BEFORE));
            }
            intercept(invocation);
            onEvent(executionContext, new NodeFragment(element), VisitSequence.BEFORE);
        } else {
            intercept(invocation);
        }
    }

    @Override
    public void visitAfter(Element element, ExecutionContext executionContext) {
        intercept(new Invocation<AfterVisitor>() {
            @Override
            public Object invoke(AfterVisitor visitor) {
                visitor.visitAfter(element, executionContext);
                return null;
            }

            @Override
            public Class<AfterVisitor> getTarget() {
                return AfterVisitor.class;
            }
        });
        
        if (getTarget() instanceof AfterVisitor) {
            onEvent(executionContext, new NodeFragment(element), VisitSequence.AFTER);
        }
    }
    
    @Override
    public void visitChildText(CharacterData characterData, ExecutionContext executionContext) {
        intercept(new Invocation<ChildrenVisitor>() {
            @Override
            public Object invoke(ChildrenVisitor visitor) {
                visitor.visitChildText(characterData, executionContext);
                return null;
            }

            @Override
            public Class<ChildrenVisitor> getTarget() {
                return ChildrenVisitor.class;
            }
        });
        
        if (getTarget() instanceof ChildrenVisitor) {
            onEvent(executionContext, new NodeFragment(characterData), VisitSequence.AFTER);
        }
    }

    @Override
    public void visitChildElement(Element childElement, ExecutionContext executionContext) {
        intercept(new Invocation<ChildrenVisitor>() {
            @Override
            public Object invoke(ChildrenVisitor visitor) {
                visitor.visitChildElement(childElement, executionContext);
                return null;
            }

            @Override
            public Class<ChildrenVisitor> getTarget() {
                return ChildrenVisitor.class;
            }
        });
        if (getTarget() instanceof ChildrenVisitor) {
            onEvent(executionContext, new NodeFragment(childElement.getParentNode()), VisitSequence.AFTER);
        }
    }

    private void onEvent(final ExecutionContext executionContext, final Fragment fragment, final VisitSequence visitSequence) {
        for (ExecutionEventListener executionEventListener : executionContext.getContentDeliveryRuntime().getExecutionEventListeners()) {
            executionEventListener.onEvent(new VisitEvent<>(fragment, getTarget(), visitSequence, executionContext));
        }
    }
}