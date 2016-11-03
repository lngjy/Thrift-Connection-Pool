/**
 *  				Copyright 2015 Jiang Wei
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.wmz7year.thrift.pool;

import org.apache.thrift.TServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 连接池内部连接数量监控线程,监控线程是否被重新连接起来了。
 * 
 * @author guojy
 * @version V1.0
 */
public class PoolRetryWatchThread<T extends TServiceClient> implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(PoolRetryWatchThread.class);

	/**
	 * 连接池对象
	 */
	private ThriftConnectionPool<T> thriftConnectionPool;

	/**
	 * 连接分区对象
	 */
	private ThriftConnectionPartition<T> thriftConnectionPartition;


	private boolean run;

	public PoolRetryWatchThread(ThriftConnectionPartition<T> thriftConnectionPartition,
                                ThriftConnectionPool<T> thriftConnectionPool) {

		this.thriftConnectionPool = thriftConnectionPool;
		this.thriftConnectionPartition = thriftConnectionPartition;

		this.run = true;
	}

	/*
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (run) {
			try{
				boolean ok = thriftConnectionPool.addThriftServer(this.thriftConnectionPartition.getThriftServerInfo());
				if ( ok ){
					return;
				}else {
					try {
						TimeUnit.SECONDS.sleep(30);
					} catch (InterruptedException e1) {
						//ignore
					}
				}

			}catch (Exception e){
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e1) {
					//ignore
				}
			}

		}
	}


	/**
	 * 停止检测线程的方法
	 */
	public void stop() {
		this.run = false;
	}

}
