package plugincore;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Processor;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

public class SysInfoBuilder {

	private Map<String,String> sihm = null; 
	
	public SysInfoBuilder()
	{
		
	}
	
	 public Map<String,String> getSysInfoMap() {
	        
	    	
	    	try
	    	{
	    	sihm = new HashMap<String,String>(); 
	    	
	    	
	    	SystemInfo si = new SystemInfo();
	        OperatingSystem os = si.getOperatingSystem();
	        
	    	sihm.put("sys-os", os.toString());
	    	
	    	HardwareAbstractionLayer hal = si.getHardware();
	        
	        
	        sihm.put("cpu-core-count", String.valueOf(hal.getProcessors().length));
	    	sihm.put("cpu-sn", hal.getProcessors()[0].getSystemSerialNumber());
	        sihm.put("cpu-summary", hal.getProcessors()[0].toString());
	        sihm.put("cpu-ident", hal.getProcessors()[0].getIdentifier());
	        sihm.put("cpu-sn-ident", hal.getProcessors()[0].getIdentifier());
	        
	        sihm.put("sys-uptime", FormatUtil.formatElapsedSecs(hal.getProcessors()[0].getSystemUptime()));
	    	
	        sihm.put("memory-available", String.valueOf(hal.getMemory().getAvailable()));
	        sihm.put("memory-total", String.valueOf(hal.getMemory().getTotal()));
	        
	        
	        // CPU
	        long[] prevTicks = hal.getProcessors()[0].getSystemCpuLoadTicks();
	        //System.out.println("CPU ticks @ 0 sec:" + Arrays.toString(prevTicks));
	        // Wait a second...
	        Util.sleep(1000);
	        long[] ticks = hal.getProcessors()[0].getSystemCpuLoadTicks();
	        //System.out.println("CPU ticks @ 1 sec:" + Arrays.toString(ticks));
	        long user = ticks[0] - prevTicks[0];
	        long nice = ticks[1] - prevTicks[1];
	        long sys = ticks[2] - prevTicks[2];
	        long idle = ticks[3] - prevTicks[3];
	        long totalCpu = user + nice + sys + idle;
	        
	        StringBuilder procCpu = new StringBuilder("CPU load per processor:");
	        for (int cpu = 0; cpu < hal.getProcessors().length; cpu++) {
	            procCpu.append(String.format(" %.1f%%", hal.getProcessors()[cpu].getProcessorCpuLoadBetweenTicks() * 100));
	        }
	        
	        String cpuUser = String.valueOf(100d * user / totalCpu);
	        String cpuNice = String.valueOf(100d * nice / totalCpu);
	        String cpuSys = String.valueOf(100d * sys / totalCpu);
	        String cpuIdle = String.valueOf(100d * idle / totalCpu);
	        String cpuPerCpu = procCpu.toString();
	        
	        sihm.put("cpu-user-load", cpuUser);
	        sihm.put("cpu-nice-load", cpuNice);
	        sihm.put("cpu-sys-load", cpuSys);
	        sihm.put("cpu-idle-load", cpuIdle);
	        sihm.put("cpu-per-cpu-load", cpuPerCpu);
	        
	        // hardware: file system
	        //LOG.info("Checking File System...");
	        //System.out.println("File System:");

	        int fsCount = 0;
	        StringBuilder sbfs = new StringBuilder();
	        OSFileStore[] fsArray = hal.getFileStores();
	        for (OSFileStore fs : fsArray) {
	        	sbfs.append(String.valueOf(fsCount) + ":" + fs.getName() + ",");
	        	long usable = fs.getUsableSpace();
	            long total = fs.getTotalSpace();
	            sihm.put("fs-" + String.valueOf(fsCount) + "-available", String.valueOf(usable));
	            sihm.put("fs-" + String.valueOf(fsCount) + "-total", String.valueOf(total));
	            
	            //System.out.println(FormatUtil.formatBytes(fs.getTotalSpace()));
	            //System.out.format(" %s (%s) %s of %s free (%.1f%%)%n", fs.getName(),
	            //        fs.getDescription().isEmpty() ? "file system" : fs.getDescription(),
	            //        FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total);
	           fsCount++;
	        }
	        String fsMap = sbfs.toString().substring(0, sbfs.length() -1);
	        sihm.put("fs-map", fsMap);
	        
	        
	    	}
	        catch(Exception ex)
	    	{
	        	System.out.println("SysInfoBuilder : Error : " + ex.toString());
	    	}
	    	
	    	
	    	//add network devices
	    	getAddresses();
	    	
	    	return sihm;
	    }

	 public void getAddresses(){
		    //String found_bcast_address=null;
		     System.setProperty("java.net.preferIPv4Stack", "true"); 
		        int nicCount = 0;
		        StringBuilder sbnic = new StringBuilder();
		           
		     try
		        {
		          Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
		          
		          while (niEnum.hasMoreElements())
		          {
		            NetworkInterface ni = niEnum.nextElement();
		            if(!ni.isLoopback()){
		            	sbnic.append(String.valueOf(nicCount) + ":" + ni.getName() + ",");
		            	
		            	StringBuilder sbip = new StringBuilder();
		            	
		            	for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses())
		                {

		                  if(interfaceAddress != null)
		                  {
		                	  try{
		                	  InetAddress address = interfaceAddress.getAddress();
		                	  //System.out.println(address.getHostAddress());	
		                	  sbip.append(address.getHostAddress() + ",");
		              		  }
		                	  catch(Exception ex)
		                	  {
		                		  System.out.println(ex.toString());
		                	  }
		                	 
		                  }
		                }
		            	if(sbip.length() > 0)
		            	{
		            		String ipMap = sbip.toString().substring(0, sbip.length() -1);
		            		sihm.put("nic-" + String.valueOf(nicCount) + "-ip", ipMap);
		            	}
		            }
		          }
		          String nicMap = sbnic.toString().substring(0, sbnic.length() -1);
			      sihm.put("nic-map", nicMap);
			        
		          //return listOfBroadcasts.add(broadcast);
		        }
		        catch (Exception ex)
		        {
		          System.out.println("getAddress : " + ex.toString());
		        }
		        //return listOfAddresses;
		        //return found_bcast_address;
		}
		
		
}
