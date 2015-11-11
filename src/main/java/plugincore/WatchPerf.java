package plugincore;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import shared.MsgEvent;
import shared.MsgEventType;



public class WatchPerf {
	public Timer timer;
	private long startTS;
	private Map<String,String> wdMap;
	
	public WatchPerf() {
		  startTS = System.currentTimeMillis();
		  timer = new Timer();
	      timer.scheduleAtFixedRate(new WatchDogTask(), 5000, Integer.parseInt(PluginEngine.config.getParam("watchperftimer")));
	      wdMap = new HashMap<String,String>(); //for sending future WD messages
	      	 
	      System.out.println("CODY: Starting NetDiscovery!!! repeat in : " + PluginEngine.config.getParam("watchperftimer"));
	      MsgEvent le = new MsgEvent(MsgEventType.INFO,PluginEngine.config.getParam("region"),null,null,"WatchDog timer set to " + Integer.parseInt(PluginEngine.config.getParam("watchdogtimer")) + " milliseconds");
	      le.setParam("src_region", PluginEngine.region);
		  le.setParam("src_agent", PluginEngine.agent);
		  le.setParam("src_plugin", PluginEngine.plugin);
		  le.setParam("dst_region", PluginEngine.region);
		  PluginEngine.clog.log(le);
	      
	  }


	class WatchDogTask extends TimerTask {
		
	    public void run() {
	    	
	    	System.out.println("CODY: Starting: Discovery!");
	    	long runTime = System.currentTimeMillis() - startTS;
	    	 MsgEvent le = new MsgEvent(MsgEventType.WATCHDOG,PluginEngine.config.getParam("region"),null,null,"WatchDog timer set to " + Integer.parseInt(PluginEngine.config.getParam("watchdogtimer")) + " milliseconds");
	    	 le.setParam("src_region", PluginEngine.region);
			 le.setParam("src_agent", PluginEngine.agent);
			 le.setParam("src_plugin", PluginEngine.plugin);
			 le.setParam("dst_region", PluginEngine.region);
			 le.setParam("isGlobal", "true");
			 le.setParam("resource_id", PluginEngine.config.getParam("resource_id"));
			 le.setParam("inode_id", PluginEngine.config.getParam("inode_id"));
			 //le.setParam("perfmetric",String.valueOf(rand.nextInt(100 - 0 + 1) + 0));
			 le.setParam("runtime", String.valueOf(runTime));
			 le.setParam("timestamp", String.valueOf(System.currentTimeMillis()));
			 System.out.println("CODY: Starting: Discovery2!");
		    	
			 Map<String,String> dhm = PluginEngine.dc.getDiscoveryMap();
			 System.out.println("CODY: Starting: Discovery3!");
		    	
			 for(Entry<String,String> entry : dhm.entrySet()) 
			 {
				 le.setParam(entry.getKey(), entry.getValue());
				 System.out.println("CODY: Discovered :" + entry.getKey() + " " + entry.getValue());
			    	
			 }
			 
			 PluginEngine.clog.log(le);
			 System.out.println("CODY: End: Discovery!");
		    	
	    }
	  }

}
