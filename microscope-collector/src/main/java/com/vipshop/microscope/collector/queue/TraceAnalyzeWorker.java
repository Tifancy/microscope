package com.vipshop.microscope.collector.queue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vipshop.microscope.collector.CollectorServer;
import com.vipshop.microscope.collector.analyzer.MessageAnalyzer;
import com.vipshop.microscope.common.trace.Span;

/**
 * A thread worker analyze span. 
 * 
 * @author Xu Fei
 * @version 1.0
 */
public class TraceAnalyzeWorker implements Runnable {
	
	private final MessageAnalyzer analyzer = MessageAnalyzer.getMessageAnalyzer();
	private final LinkedBlockingQueue<Span> queue;
	
	public TraceAnalyzeWorker(LinkedBlockingQueue<Span> queue) {
		this.queue = queue;
	}
	
	@Override
	public void run() {
		while (true) {
			Span span = queue.poll();

			if (span != null) {
				analyzer.analyze(span);
			} else {
				try {
					TimeUnit.MILLISECONDS.sleep(CollectorServer.SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
