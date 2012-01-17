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
package de.scoopgmbh.copper.batcher.impl;

import java.sql.Connection;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import de.scoopgmbh.copper.batcher.BatchCommand;
import de.scoopgmbh.copper.batcher.BatchExecutor;
import de.scoopgmbh.copper.batcher.CommandCallback;
import de.scoopgmbh.copper.batcher.NullCallback;

public class BatcherImplTest extends TestCase {
	
	private static final Logger logger = Logger.getLogger(BatcherImplTest.class);
	
	static final class TestBatchCommand implements BatchCommand<TestBatchExecutor, TestBatchCommand> {

		long targetTime = System.currentTimeMillis() + 1000;
		String data;
		
		public TestBatchCommand(String data) {
			this.data = data;
		}
		
		@Override
		public CommandCallback<TestBatchCommand> callback() {
			return new NullCallback<TestBatchCommand>();
		}

		@Override
		public TestBatchExecutor executor() {
			return TestBatchExecutor.INSTANCE;
		}

		@Override
		public long targetTime() {
			return targetTime;
		}

		@Override
		public DataSource dataSource() {
			return null;
		}
		
	};
	
	static final class TestBatchExecutor extends BatchExecutor<TestBatchExecutor, TestBatchCommand> {
		
		public static TestBatchExecutor INSTANCE = new TestBatchExecutor();

		@Override
		protected void doExec(final Collection<BatchCommand<TestBatchExecutor, TestBatchCommand>> commands, final Connection con) throws Exception {
			logger.debug("new batch:");
			for (BatchCommand<TestBatchExecutor, TestBatchCommand> cmd : commands) {
				logger.debug(((TestBatchCommand)cmd).data);
			}
		}

		@Override
		public int maximumBatchSize() {
			return 100;
		}

		@Override
		public int preferredBatchSize() {
			return 50;
		}
		
	}
	
	

	public final void testSubmitBatchCommand() throws InterruptedException {
		BatcherImpl batcher = new BatcherImpl(2);
		batcher.startup();
		try {
			for (int i=0; i<100; i++) {
				batcher.submitBatchCommand(new TestBatchCommand("Test#"+i));
			}
			Thread.sleep(5000);
		}
		finally {
			batcher.shutdown();
		}
	}

}
