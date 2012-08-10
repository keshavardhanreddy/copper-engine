/*
 * Copyright 2002-2012 SCOOP Software GmbH
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
package de.scoopgmbh.copper.test.tranzient.simple;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.scoopgmbh.copper.EngineState;
import de.scoopgmbh.copper.Workflow;
import de.scoopgmbh.copper.WorkflowFactory;
import de.scoopgmbh.copper.tranzient.TransientProcessorPool;
import de.scoopgmbh.copper.tranzient.TransientScottyEngine;
import de.scoopgmbh.copper.util.AsyncResponseReceiver;
import de.scoopgmbh.copper.util.BlockingResponseReceiver;

public class SupendPoolTransientEngineTest extends TestCase {
	
	public void testWorkflow() throws Exception {
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"transient-engine-application-context.xml", "SimpleTransientEngineTest-application-context.xml"});
		TransientScottyEngine engine = (TransientScottyEngine) context.getBean("transientEngine");
		TransientProcessorPool processorPool = (TransientProcessorPool) context.getBean("T_ProcessorPool_DEFAULT");
		assertEquals(EngineState.STARTED,engine.getEngineState());
		
		try {
			final BlockingResponseReceiver<Integer> brr = new BlockingResponseReceiver<Integer>();
			Thread.sleep(10);
			Assert.assertFalse(brr.isResponseReceived());
			
			processorPool.suspend();
			
			engine.run("de.scoopgmbh.copper.test.tranzient.simple.NopTransientWorkflow", brr);

			brr.wait4response(100L);
			
			Assert.assertFalse(brr.isResponseReceived());

			processorPool.resume();
			
			brr.wait4response(100L);
			
			assertEquals(1,brr.getResponse().intValue());
		}
		finally {
			context.close();
		}
		assertEquals(EngineState.STOPPED,engine.getEngineState());
		
	}
	
}