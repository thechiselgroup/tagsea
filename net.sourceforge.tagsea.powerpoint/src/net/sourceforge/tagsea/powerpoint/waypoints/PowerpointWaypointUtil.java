package net.sourceforge.tagsea.powerpoint.waypoints;

public class PowerpointWaypointUtil 
{
	
	public static int[] getSlideRange(String rangeString) 
	{
		String[] ranges = rangeString.trim().split("-");
		
		if(ranges.length == 2)
		{
			try 
			{
				int start = Integer.parseInt(ranges[0]);
				int end = Integer.parseInt(ranges[1]);
				
				return new int[]{start,end};
			} 
			catch (NumberFormatException e) 
			{
			}
		}
		return null;
	}

}
