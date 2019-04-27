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
	double m=2.0;//��Ȩָ��
	double line=0.6;//FCM��ֵ
	public ArrayList<Point> service=new ArrayList<Point>();
	public ArrayList<ClusterService> services=new ArrayList<ClusterService>();//����������
	public ArrayList<Cluster> clusters=new ArrayList<Cluster>();//��ž���
	double ma[][];//���ƶȾ���
	double u[][];//�����Ⱦ���
	int center[];
	
	double E[];
	double W[];
	
	//�ⲿ��ȡcenter
	public void setCenter(int[] newcenter)
	{
		//ȷ����Ӧ������
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
	
	//��ʼ������
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
	
	//��ʼ�����ƶȾ���
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
	
	//��ֵ����
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
	
	//ͨ�������������Ⱦ���������Ķ��󼯺�
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
	
	//��ʼ�������Ⱦ���
	public void initrate_sa()
	{
		center=new int[k];
		u=new double[services.size()][k];
		
		double [][]temple=new double[services.size()][k];
		int c[]=new int[k];
		//��ʼ��������
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
		 *  ����Ŷ�
		 * ����ѡ��
		*/
		int t=1000;
		while(t>=1)
		{
			t--;
			//������������ݴ�
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
				//�˻�ѡȡ
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
	
	//����Ȩ������
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
		//������������
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
			if(formertype!=fcs.type)//��������ȼ�Ⱥ�仯��
			{
				clusters.get(formertype).services.remove(fcs);
				clusters.get(fcs.type).services.add(fcs);
			}
		}
		//�����µ�center	
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
			clusters.get(fcs.type).services.add(fcs);//�Ƚ�����ֵ����Ծ�����
		}
		for(int i=0;i<k;i++)//���ĳ�ʼ��
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
		initrate_sa();//�˻��ʼ��������
		fcminit();//��ʼ����Ⱥ������
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
