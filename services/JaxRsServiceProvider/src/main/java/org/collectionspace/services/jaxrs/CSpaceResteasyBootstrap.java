package org.collectionspace.services.jaxrs;

import javax.servlet.ServletContextEvent;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.collectionspace.services.common.ResourceMap;
import java.util.Date;

public class CSpaceResteasyBootstrap extends ResteasyBootstrap {
	
	public void  contextInitialized(ServletContextEvent event) {
		try {
			//
	    	// This call to super instantiates and initializes our JAX-RS application class.
	    	// The application class is org.collectionspace.services.jaxrs.CollectionSpaceJaxRsApplication.
	    	//
	    	System.out.println(String.format("%tc [INFO] Starting up the CollectionSpace Services' JAX-RS application.", new Date()));
			super.contextInitialized(event);
			CollectionSpaceJaxRsApplication app = 
				(CollectionSpaceJaxRsApplication)deployment.getApplication();
			Dispatcher disp = deployment.getDispatcher();
			disp.getDefaultContextObjects().put(ResourceMap.class, app.getResourceMap());
	    	System.out.println(String.format("%tc [INFO] CollectionSpace Services' JAX-RS application started.", new Date()));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public void contextDestroyed(ServletContextEvent event) {
    	System.out.println("[INFO] Shutting down the CollectionSpace Services' JAX-RS application.");
    	//Do something if needed.
    	System.out.println("[INFO] CollectionSpace Services' JAX-RS application stopped.");
    }	

}
