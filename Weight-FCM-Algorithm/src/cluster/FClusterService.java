package cluster;

import elements.Point;

public class FClusterService 
{
	public int num;
	public int type;
	public int k;//������Ŀ
	Point service;
	public double []rate;//����Ⱥ��������
	public void setType()
	{
		double max=0;
		for(int i=0;i<k;i++)
		{
			if(max<rate[i])
			{
				max=rate[i];
				type=i;
			}
		}
	}
	public void setrate(double r[])
	{
		rate=r;
	}
	public void setService(Point s)
	{
		service=s;
	}
	public double getSimilarity(FClusterService cs)
	{
		return service.getSimilarity(cs.service);
	}
}
