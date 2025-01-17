/*-
 * ========================LICENSE_START=================================
 * Core
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
package org.smooks.engine.delivery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.smooks.Smooks;
import org.smooks.api.ExecutionContext;
import org.smooks.engine.resource.visitor.dom.DOMModel;
import org.smooks.support.StreamUtils;
import org.smooks.support.XmlUtil;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;

import java.io.IOException;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class DomModelCreatorTestCase {

	@BeforeEach
    public void setUp() throws Exception {
        ModelCatcher.elements.clear();
    }

	@Test
    public void test_sax_01() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("node-model-01.xml"));
        
        ExecutionContext executionContext = smooks.createExecutionContext();
        smooks.filterSource(executionContext, new StreamSource(getClass().getResourceAsStream("order-message.xml")), null);

        DOMModel nodeModel = DOMModel.getModel(executionContext);

        assertTrue(
                StreamUtils.compareCharStreams(
                "<order>\n" +
                "    <header>\n" +
                "        <date>Wed Nov 15 13:45:28 EST 2006</date>\n" +
                "        <customer number=\"123123\">Joe &gt; the man</customer>\n" +
                "    </header>\n" +
                "    <order-items/>\n" +
                "</order>",
                XmlUtil.serialize(nodeModel.getModels().get("order"), true, true)));

        assertTrue(
                StreamUtils.compareCharStreams(
                "        <order-item>\n" +
                "            <product>222</product>\n" +
                "            <quantity>7</quantity>\n" +
                "            <price>5.20</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(nodeModel.getModels().get("order-item"), true, true)));

        // Check all the order-item model added...
        assertEquals(2, ModelCatcher.elements.size());
        assertTrue(
                StreamUtils.compareCharStreams(
                "        <order-item>\n" +
                "            <product>111</product>\n" +
                "            <quantity>2</quantity>\n" +
                "            <price>8.90</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(ModelCatcher.elements.get(0), true, true)));
        assertTrue(
                StreamUtils.compareCharStreams(
                "        <order-item>\n" +
                "            <product>222</product>\n" +
                "            <quantity>7</quantity>\n" +
                "            <price>5.20</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(ModelCatcher.elements.get(1), true, true)));
    }

	@Test
    public void test_sax_02() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("node-model-02.xml"));
        
        ExecutionContext executionContext = smooks.createExecutionContext();
        smooks.filterSource(executionContext, new StreamSource(getClass().getResourceAsStream("order-message.xml")), null);

        DOMModel nodeModel = DOMModel.getModel(executionContext);

        assertTrue(
                StreamUtils.compareCharStreams(
                "<order>\n" +
                "    <header>\n" +
                "        <date>Wed Nov 15 13:45:28 EST 2006</date>\n" +
                "        <customer number=\"123123\">Joe &gt; the man</customer>\n" +
                "    </header>\n" +
                "    <order-items/>\n" +
                "</order>",
                XmlUtil.serialize(nodeModel.getModels().get("order"), true, true)));

        assertTrue(
                StreamUtils.compareCharStreams(
                "        <order-item>\n" +
                "            <quantity>7</quantity>\n" +
                "            <price>5.20</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(nodeModel.getModels().get("order-item"), true, true)));

        assertTrue(
                StreamUtils.compareCharStreams(
                "<product>222</product>",
                XmlUtil.serialize(nodeModel.getModels().get("product"), true, true)));

        // Check all the order-item model added...
        assertEquals(4, ModelCatcher.elements.size());
        assertTrue(
                StreamUtils.compareCharStreams(
                "<product>111</product>",
                XmlUtil.serialize(ModelCatcher.elements.get(0), true, true)));
        assertTrue(
                StreamUtils.compareCharStreams(
                "<order-item>\n" +
                "            <quantity>2</quantity>\n" +
                "            <price>8.90</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(ModelCatcher.elements.get(1), true, true)));
        assertTrue(
                StreamUtils.compareCharStreams(
                "<product>222</product>",
                XmlUtil.serialize(ModelCatcher.elements.get(2), true, true)));
        assertTrue(
                StreamUtils.compareCharStreams(
                "<order-item>\n" +
                "            <quantity>7</quantity>\n" +
                "            <price>5.20</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(ModelCatcher.elements.get(3), true, true)));
    }

	@Test
    public void test_sax_03() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("node-model-02.xml"));
        
        ExecutionContext executionContext = smooks.createExecutionContext();
        smooks.filterSource(executionContext, new StreamSource(getClass().getResourceAsStream("order-message-with-ns.xml")), null);

        DOMModel nodeModel = DOMModel.getModel(executionContext);

        assertTrue(
                StreamUtils.compareCharStreams(
                "<order>\n" +
                "    <header>\n" +
                "        <date>Wed Nov 15 13:45:28 EST 2006</date>\n" +
                "        <customer number=\"123123\">Joe &gt; the man</customer>\n" +
                "    </header>\n" +
                "    <order-items/>\n" +
                "</order>",
                XmlUtil.serialize(nodeModel.getModels().get("order"), true, true)));

        assertTrue(
                StreamUtils.compareCharStreams(
                "        <ordi:order-item xmlns:ordi=\"http://ordi\">\n" +
                "            <ordi:quantity>7</ordi:quantity>\n" +
                "            <ordi:price>5.20</ordi:price>\n" +
                "        </ordi:order-item>",
                XmlUtil.serialize(nodeModel.getModels().get("order-item"), true, true)));

        assertTrue(
                StreamUtils.compareCharStreams(
                "<ordi:product xmlns:ordi=\"http://ordi\">222</ordi:product>",
                XmlUtil.serialize(nodeModel.getModels().get("product"), true, true)));
    }

	@Test
    @Disabled
    public void test_dom() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("node-model-01.xml"));
        ExecutionContext executionContext = smooks.createExecutionContext();

        smooks.filterSource(executionContext, new StreamSource(getClass().getResourceAsStream("order-message.xml")));

        DOMModel nodeModel = DOMModel.getModel(executionContext);

        assertTrue(
                StreamUtils.compareCharStreams(
                "<order>\n" +
                "    <header>\n" +
                "        <date>Wed Nov 15 13:45:28 EST 2006</date>\n" +
                "        <customer number=\"123123\">Joe &gt; the man</customer>\n" +
                "    </header>\n" +
                "    <order-items>\n" +
                "        <order-item>\n" +
                "            <product>111</product>\n" +
                "            <quantity>2</quantity>\n" +
                "            <price>8.90</price>\n" +
                "        </order-item>\n" +
                "        <order-item>\n" +
                "            <product>222</product>\n" +
                "            <quantity>7</quantity>\n" +
                "            <price>5.20</price>\n" +
                "        </order-item>\n" +
                "    </order-items>\n" +
                "</order>\n",
                XmlUtil.serialize(nodeModel.getModels().get("order"), true, true)));

        assertTrue(
                StreamUtils.compareCharStreams(
                "        <order-item>\n" +
                "            <product>222</product>\n" +
                "            <quantity>7</quantity>\n" +
                "            <price>5.20</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(nodeModel.getModels().get("order-item"), true, true)));

        // Check all the order-item model added...
        assertEquals(2, ModelCatcher.elements.size());
        assertTrue(
                StreamUtils.compareCharStreams(
                "        <order-item>\n" +
                "            <product>111</product>\n" +
                "            <quantity>2</quantity>\n" +
                "            <price>8.90</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(ModelCatcher.elements.get(0), true, true)));
        assertTrue(
                StreamUtils.compareCharStreams(
                "        <order-item>\n" +
                "            <product>222</product>\n" +
                "            <quantity>7</quantity>\n" +
                "            <price>5.20</price>\n" +
                "        </order-item>",
                XmlUtil.serialize(ModelCatcher.elements.get(1), true, true)));
    }

}
