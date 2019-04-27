package FCM;

import java.util.ArrayList;

import cluster.Cluster;
import cluster.ClusterService;
import cluster.FCluster;
import cluster.FClusterService;
import elements.Point;

public class FCM_SA 
{
	public int k=5;
	double m=2.0;//加权指数
	double line=0.6;//FCM阈值
	public ArrayList<Point> service=new ArrayList<Point>();
	public ArrayList<ClusterService> services=new ArrayList<ClusterService>();//聚类服务对象
	public ArrayList<Cluster> clusters=new ArrayList<Cluster>();//存放聚类
	double ma[][];//相似度矩阵
	double u[][];//隶属度矩阵
	int center[];
	
	double E[];
	double W[];
	
	//外部获取center
	public void setCenter(int[] newcenter)
	{
		//确定对应的中心
		for(int i=0;i<k;i++)
		{
			
		}
	}
	public int[] getCenter()
	{
		return center;
	}
	public void setMa(double ma[][])
	{
		this.ma=ma;
	}
	public void setService(ArrayList<Point> service)
	{
		this.service=service;
	}
	
	
	public double[] raterandom()
	{
		double r[]=new double[k];
		double sum=1.0;
		for(int i=0;i<k-1;i++)
		{
			double ran=Math.random()*sum;
			r[i]=ran;
			sum-=ran;
		}
		r[k-1]=sum;
		return r;
	}
	
	//初始化服务
	public void initService()
	{
		for(int i=0;i<service.size();i++)
		{
			ClusterService fcs=new ClusterService();
			fcs.setService(service.get(i));
			fcs.num=i;
			fcs.type=i;
			services.add(fcs);
		}
	}
	
	//初始化相似度矩阵
	void initMatrix()
	{
		ma=new double[services.size()][services.size()];
		for(int i=0;i<services.size();i++)
			for(int j=0;j<services.size();j++)
			{
				if(i!=j)
					ma[i][j]=services.get(i).getSimilarity(services.get(j));
				else
					ma[i][j]=0;
			}
	}
	boolean iscenter(int num)
	{
		for(int i=0;i<k;i++)
		{
			if(num==center[i])
				return true;
		}
		return false;
	}
	
	//价值函数
	double value(double[][] u,int []center)
	{
		double sum=0;
		for(int i=0;i<services.size();i++)
		{
			if(!iscenter(i))
			{
				double value=0;
				for(int j=0;j<k;j++)
					value+=ma[i][center[j]]*(u[i][j]);
				sum+=value;
			}
		}
		return sum;
	}
	
	//通过给定的隶属度矩阵计算中心对象集合
	int[] getcenternow(double [][]u)
	{
		ArrayList<Cluster> clusters=new ArrayList<Cluster>();
		for(int i=0;i<k;i++)
			clusters.add(new Cluster());
		int r[]=new int[k];
		
		for(ClusterService s:services)
		{
			s.setFCMtype(u, k);
			clusters.get(s.type).services.add(s);
		}
		for(int i=0;i<k;i++)
		{
			clusters.get(i).setcenter(ma);
			r[i]=clusters.get(i).center;
		}
		return r;
	}
	
	//初始化隶属度矩阵
	public void initrate_sa()
	{
		center=new int[k];
		u=new double[services.size()][k];
		
		double [][]temple=new double[services.size()][k];
		int c[]=new int[k];
		//初始化隶属度
		for(int i=0;i<services.size();i++)
		{
			double r[]=raterandom();
			u[i]=r;
		}
		
		center=getcenternow(u);
		double oldvalue=value(u,center);
		double newvalue=0;
		//sa
		/*
		 *  随机扰动
		 * 进行选择
		*/
		int t=1000;
		while(t>=1)
		{
			t--;
			//随机新隶属度暂存
			for(int i=0;i<services.size();i++)
			{
				double r[]=raterandom();
				temple[i]=r;
			}
			c=getcenternow(u);
			newvalue=value(temple,c);
			//System.out.println("sa:"+newvalue);
			if(newvalue>=oldvalue)
			{
				oldvalue=newvalue;
				u=temple;
				center=c;
			}
			else
			{
				//退火选取
				double rate=Math.exp((newvalue-oldvalue)/t);
				double rrate=Math.random();
				if(rrate<=rate)
				{
					oldvalue=newvalue;
					u=temple;
					center=c;
				}
			}
		}	
	}
	
	//计算权重序列
	public void initW()
	{	
		E=new double[services.size()];
		W=new double[services.size()];
		double Esum=0;	
		for(int i=0;i<services.size();i++)
		{
			double sum=0;
			for(int j=0;j<k;j++)
			{
				sum+=-(u[i][j]*(Math.log(u[i][j])/Math.log(2)));
			}
			E[i]=sum;
			Esum+=E[i];
		}
			
		for(int i=0;i<services.size();i++)
		{
			double up=1-E[i];
			W[i]=up/(services.size()-Esum);
		}
	}
	
	public void fcmstep()
	{
		//生成新隶属度
		for(int i=0;i<services.size();i++)
		{
			if(!iscenter(i))
			{
				for(int j=0;j<k;j++)
				{
					double sum=0;
					for(int p=0;p<k;p++)
					{
						sum+=Math.pow(((1/ma[i][center[j]])/(1/ma[i][center[p]])),1/(m-1));
						sum=1/sum;
					}
					u[i][j]=sum;
				}
			}
		}
		
		for(ClusterService fcs:services)
		{
			int formertype=fcs.type;
			fcs.setFCMtype(u, k);	
			if(formertype!=fcs.type)//最大隶属度集群变化了
			{
				clusters.get(formertype).services.remove(fcs);
				clusters.get(fcs.type).services.add(fcs);
			}
		}
		//计算新的center	
		for(int i=0;i<k;i++)
		{
			Cluster fc=clusters.get(i);
			fc.setcenter(ma);
			center[i]=fc.center;
		}
	}
		
	public void fcminit()
	{
		for(int i=0;i<k;i++)
		{
			Cluster fc=new Cluster();
			clusters.add(fc);
		}
		for(ClusterService fcs:services)
		{
			fcs.setFCMtype(u, k);
			clusters.get(fcs.type).services.add(fcs);//先将服务分到各自聚类中
		}
		for(int i=0;i<k;i++)//中心初始化
		{
			Cluster fc=clusters.get(i);
			fc.setcenter(ma);
			center[i]=fc.center;
		}
	}
	
	public void start()
	{
		initService();
		initMatrix();
		initrate_sa();//退火初始化隶属度
		fcminit();//初始化集群和中心
		initW();
		int t=100;
		while(t>=0)
		{
			t--;
			fcmstep();
		}
	}
	
	public void print()
	{
		for(int i=0;i<clusters.size();i++)
		{
			System.out.println("type "+i+":");
			clusters.get(i).print();
		}
	}
}
