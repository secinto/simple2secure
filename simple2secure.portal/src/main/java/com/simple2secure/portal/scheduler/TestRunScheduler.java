package com.simple2secure.portal.scheduler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.simple2secure.api.model.Probe;

import ch.maxant.rules.AbstractAction;
import ch.maxant.rules.CompileException;
import ch.maxant.rules.DuplicateNameException;
import ch.maxant.rules.Engine;
import ch.maxant.rules.ParseException;
import ch.maxant.rules.Rule;

@Component
public class TestRunScheduler{
	
    private static final Logger log = LoggerFactory.getLogger(TestRunScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");	
    
    //@Scheduled(fixedRate = 50000)
    public void runTest() {
    	
    }   
    
    
	//@Scheduled(fixedRate = 50000)
    public void reportCurrentTime() {
        log.info("The time is now {}", dateFormat.format(new Date()));
        Rule r1 = new Rule("YouthTarif", "input.activated == true", "notificationAction", 3, "com.simple2secure.api.model.Device", null);
        Rule r2 = new Rule("SeniorTarif", "input.activated == false", "notificationAction", 3, "com.simple2secure.api.model.Device", null);
        Rule r3 = new Rule("LoyaltyTarif", "#YouthTarif && input.name == 'dev1'", "notificationAction", 4, "com.simple2secure.api.model.Device", null);
        
        List<Rule> rules = Arrays.asList(r1, r2, r3);
        
        try {
        	
    		AbstractAction<Probe, Void> a1 = new AbstractAction<Probe, Void>("notificationAction") {
    			@Override
    			public Void execute(Probe input) {
    				log.info("Sending email to user!");
    				return null;
    			}
    		};
    		
    		AbstractAction<Probe, Void> a2 = new AbstractAction<Probe, Void>("secondAction") {
    			@Override
    			public Void execute(Probe input) {
    				log.info("Sending email to user 2!");
    				return null;
    			}
    		};  		
			
			Engine engine = new Engine(rules, true);
			//Probe dev1 = new Probe("dev1", "userid", "clientId", "macAddress", "timskaodksaodsa", true);
			//Probe dev2 = new Probe("dev2", "userid", "clientId", "macAddress", "timskaodksaodsa", false);
			
			/*try {
				engine.executeAllActions(dev1, Arrays.asList(a1, a2));
			} catch (NoMatchingRuleFoundException | NoActionFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		} catch (DuplicateNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

}
