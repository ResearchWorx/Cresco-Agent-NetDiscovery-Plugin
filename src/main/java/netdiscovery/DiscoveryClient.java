package netdiscovery;

import java.util.Map;

import plugincore.PluginEngine;

public class DiscoveryClient 
{
	private int discoveryTimeout;
	public DiscoveryClient()
	{
		discoveryTimeout = Integer.parseInt(PluginEngine.config.getParam("discoverytimeout")); 
		System.out.println("DiscoveryClient : discoveryTimeout = " + discoveryTimeout);
	}
	
	public Map<String,String> getDiscoveryMap()
	{
		Map<String,String> disMap = null;
		
		try
		{
			while(PluginEngine.clientDiscoveryActive)
			{
				System.out.println("DiscoveryClient : Discovery already underway : waiting..");
				Thread.sleep(2500);
			}
			PluginEngine.clientDiscoveryActive = true;
			DiscoveryClientWorker dcw = new DiscoveryClientWorker(discoveryTimeout);
			disMap = dcw.getDiscoveryMap();
		}
		catch(Exception ex)
		{
			System.out.println("DiscoveryClient Error : " + ex.toString());
		}
		PluginEngine.clientDiscoveryActive = false;
		
		return disMap;
	}
	


}
