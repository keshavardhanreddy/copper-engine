/*
 * Copyright 2002-2014 SCOOP Software GmbH
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
package org.copperengine.core.test.tranzient.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.copperengine.core.EngineState;
import org.copperengine.core.Workflow;
import org.copperengine.core.test.TestResponseReceiver;
import org.copperengine.core.tranzient.TransientScottyEngine;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AbstractIssueTest {

    private final int[] response = { -1 };

    @Test
    public void testWorkflow() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "transient-engine-application-context.xml", "SimpleTransientEngineTest-application-context.xml" });
        TransientScottyEngine engine = context.getBean("transientEngine", TransientScottyEngine.class);
        context.getBeanFactory().registerSingleton("OutputChannel4711", new TestResponseReceiver<String, Integer>() {
            @Override
            public void setResponse(Workflow<String> wf, Integer r) {
                synchronized (response) {
                    response[0] = r.intValue();
                    response.notifyAll();
                }
            }
        });

        assertEquals(EngineState.STARTED, engine.getEngineState());

        try {
            final CompletionIndicator data = new CompletionIndicator();
            engine.run("org.copperengine.core.test.tranzient.simple.IssueClassCastExceptionWorkflow", data);

            Thread.sleep(2500);

            assertTrue(data.done);
            assertFalse(data.error);
        } finally {
            context.close();
        }
        assertEquals(EngineState.STOPPED, engine.getEngineState());

    }

}
