package plugincore;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SubnodeConfiguration;

public class PluginConfig {

	private SubnodeConfiguration configObj; 
	  
	
	public PluginConfig(SubnodeConfiguration configObj) throws ConfigurationException
	{
	    this.configObj = configObj;
	}
	public Boolean webDb()
	{
		if(configObj.getString("webdb").equals("1"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public String getParam(String param)
	{
		String getparam = null;
		try
		{
			getparam = configObj.getString(param);
		}
		catch(Exception ex)
		{
			System.out.println("PluginConfig : Param not found !" + ex.toString());
		}
		return getparam;
		
	}
	
	public boolean setParam(String paramKey, String paramValue, boolean overWrite)
	{
		boolean isSet = false;
		try
		{
			String param = configObj.getString(paramKey);
			if((param != null) && !overWrite)
			{
				System.out.println("PluginConfig : setParam : Tried to overwrite existing param!");
			}
			else
			{
				configObj.setProperty(paramKey, paramValue);
				String checkValue = configObj.getString(paramKey);
				if(checkValue.equals(paramValue))
				{
					isSet = true;
				}
			}
			
		}
		catch(Exception ex)
		{
			System.out.println("PluginConfig : Param not found !" + ex.toString());
		}
		return isSet;
		
	}
	
	public Map<String,String> getPluginConfigMap()
	{
		final Map<String,String> result=new TreeMap<String,String>();
		  final Iterator it=configObj.getKeys();
		  while (it.hasNext()) {
		    final Object key=it.next();
		    final String value=configObj.getString(key.toString());
		    result.put(key.toString(),value);
		  }
		  return result;	
	}
	
	public String getPluginConfigString()
	{
		//final Map<String,String> result=new TreeMap<String,String>();
		  StringBuilder sb = new StringBuilder();
			final Iterator it=configObj.getKeys();
		  while (it.hasNext()) {
		    final Object key=it.next();
		    final String value=configObj.getString(key.toString());
		    //result.put(key.toString(),value);
		    sb.append(key.toString() + "=" + value + ",");
		  }
		  return sb.toString().substring(0, sb.length() -1);
		  //return result;	
	}
	
	/*
	public int getWatchDogTimer()
	{
		return configObj.getInt("watchdogtimer");
	}
	*/
}