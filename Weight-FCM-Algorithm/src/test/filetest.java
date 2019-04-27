package test;

import java.util.ArrayList;
import java.util.List;

import agnes_keans.Agnes_Kmeans;
import cluster.Cluster;
import elements.Point;
import elements.testService;
import fileread.FileAna;

public class filetest {
	public static void main(String[] args)
	{
		int size=2048;
		FileAna fa=new FileAna();
		String path="simmatrix.txt";
		String filepath="mashup.xlsx";
		fa.initM(path, size);//��ȡ������Ϣ
		fa.initNames(filepath);//��ȡ������Ϣ
		List<String> names=fa.getNames();
		ArrayList<Point> services=new ArrayList<Point>();
		
		for(int i=0;i<size;i++)
			{
				services.add(new testService(names.get(i)));
			}
		
		double sumrate=0;
		for(double round=0.1;round<1;round+=0.05)
		{
			Agnes_Kmeans ak=new Agnes_Kmeans();
			ak.k=5;
			ak.setR(round);//�����ܶ�ѡȡ��Χ
			ak.setServices(services);
			ak.initService();//��ʼ������
			ak.setMatrix(fa.getM());
			
			
			if(services.size()>ak.k)//kmeansԤ����
			{
				ak.initDensity();
								
				ak.kmeans();
			}
			
			//ak.agnes();
			//ak.print();
			
			ArrayList<Cluster> clusters=ak.getClusters();		
			
			//����׼ȷ��
			double rate=correctrate.calcorrect(clusters, size);
			sumrate+=rate;
			System.out.println("�ܶ�ѡȡ��ΧΪ "+round);
			System.out.println("rate:"+rate);
		}
		
		//System.out.println("avg:"+sumrate/((1-0.1)/0.05-1));
	}
}
