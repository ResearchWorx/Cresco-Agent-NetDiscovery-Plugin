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
	      timer.scheduleAtFixedRate(new WatchDogTask(), 1000, Integer.parseInt(PluginEngine.config.getParam("watchperftimer")));
	      wdMap = new HashMap<String,String>(); //for sending future WD messages
	      	  
	      MsgEvent le = new MsgEvent(MsgEventType.INFO,PluginEngine.config.getParam("region"),null,null,"WatchDog timer set to " + Integer.parseInt(PluginEngine.config.getParam("watchdogtimer")) + " milliseconds");
	      le.setParam("src_region", PluginEngine.region);
		  le.setParam("src_agent", PluginEngine.agent);
		  le.setParam("src_plugin", PluginEngine.plugin);
		  le.setParam("dst_region", PluginEngine.region);
		  PluginEngine.clog.log(le);
	      
	  }


	class WatchDogTask extends TimerTask {
		
	    public void run() {
	    	
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
			 
			 ArrayList<InetAddress> pl = PluginEngine.de.getPeers();
			 StringBuilder sb = new StringBuilder();
			 for(InetAddress inet : pl)
			 {
				 sb.append(inet.getHostAddress() + ",");
			 }
			 if(sb.length() > 0)
			 {
				 String discoveredIp = sb.substring(0, sb.length() -1);
				 le.setParam("discoveredList", discoveredIp);
			 }
			 else
			 {
				 le.setParam("discoveredList", "");
			 }
			 
			 PluginEngine.clog.log(le);
	    }
	  }

}
