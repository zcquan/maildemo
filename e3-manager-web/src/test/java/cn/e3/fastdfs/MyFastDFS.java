package cn.e3.fastdfs;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.e3.manager.utils.FastDFSClient;

public class MyFastDFS {
	
	
	/**
	 * 需求:使用fastDFS提供客户端api实现图片上传
	 * @throws Exception 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void uploadPicTest01() throws Exception{
		
		//指定上传图片
		String pic = "E:\\image\\Penguins.jpg";
		//指定配置文件绝对路径
		String client = "E:\\hubin\\javaee68\\itheima68\\"
				+ "e3-manager-web\\src\\main\\resources\\conf\\client.conf";
		
		//是否fastDFS客户端api加载client.conf配置文件,连接tracker_server服务器
		ClientGlobal.init(client);
		
		//创建trackerServer服务对象
		TrackerClient tClient = new TrackerClient();
		//从客户端对象中获取服务对象
		TrackerServer trackerServer = tClient.getConnection();
		
		StorageServer storageServer=null;
		//创建storage存储服务器客户端对象
		StorageClient sClient = new StorageClient(trackerServer, storageServer);
		
		//上传
		String[] urls = sClient.upload_file(pic, "jpg", null);
		
		for (String url: urls) {
			System.out.println(url);
			
		}
		
	}
	
	
	/**
	 * 需求:使用fastDFS提供工具类实现图片上传
	 * @throws Exception 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void uploadPicTest02() throws Exception{
		
		//指定上传图片
		String pic = "E:\\image\\Chrysanthemum.jpg";
		//指定配置文件绝对路径
		String client = "E:\\hubin\\javaee68\\itheima68\\"
				+ "e3-manager-web\\src\\main\\resources\\conf\\client.conf";
		
		//创建工具类对象
		FastDFSClient fClient = new FastDFSClient(client);
		
		//上传
		String url = fClient.uploadFile(pic);
		
		System.out.println(url);
		
	}

}
