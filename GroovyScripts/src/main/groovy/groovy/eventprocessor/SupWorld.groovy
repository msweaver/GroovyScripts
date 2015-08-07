package groovy.eventprocessor

import org.springframework.stereotype.Component
import org.tiaa.cth.event.EventProcessor
import org.tiaa.cth.event.model.Event;

@Component("groovyEventProcessor")
class SupWorld implements EventProcessor {

	@Override
	public void confirmDelivery(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void processEvent(Event arg0) {
		System.out.println "sup world";
	}

}
