package test;

import java.util.ArrayList;
import java.util.List;

import FCM.FCM_SA;
import cluster.Cluster;
import elements.Point;
import elements.testService;
import fileread.FileAna;

public class FcmFiletest {
	public static void main(String[] args)
	{
		int size=2048;
		FileAna fa=new FileAna();
		String path="simmatrix.txt";
		String filepath="mashup.xlsx";
		fa.initM(path, size);//读取矩阵信息
		fa.initNames(filepath);//读取服务信息
		List<String> names=fa.getNames();
		ArrayList<Point> services=new ArrayList<Point>();
		for(int i=0;i<size;i++)
		{
			services.add(new testService(names.get(i)));
		}
		
		double sumrate=0;				
		for(int round=0;round<1;round++)
		{
			FCM_SA fs=new FCM_SA();
			fs.setService(services);
			fs.initService();
			//System.out.println("servicenum "+fs.services.size());
			fs.setMa(fa.getM());
			fs.initrate_sa();
			fs.fcminit();//初始化集群和中心
			fs.initW();
			int t=100;
			while(t>=0)
			{
				t--;
				fs.fcmstep();
			}
			//fs.print();
			
			ArrayList<Cluster> clusters=fs.clusters;
			/*for(Cluster c:clusters)
				System.out.println(c.services.size());*/
			
			//计算准确率
			double rate=correctrate.calcorrect(clusters, size);
			sumrate+=rate;
			System.out.println("round "+round+" "+rate);
		}
		
		//System.out.println("avg:"+sumrate/10);
	}
}
