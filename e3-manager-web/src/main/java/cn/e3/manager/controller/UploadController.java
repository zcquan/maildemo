package cn.e3.manager.controller;

import javax.management.openmbean.InvalidKeyException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3.manager.utils.FastDFSClient;
import cn.e3.utils.JsonUtils;
import cn.e3.utils.KindEditorModel;

@Controller
public class UploadController {
	
	//注入图片服务器地址
	@Value("${IMAGE_URL}")
	private String IMAGE_URL;
	
	/**
	 * 需求:使用fastDFs分布式文件系统上传图片
	 * 请求：/pic/upload
	 * 参数：uploadFile
	 * 返回值：
	 * 成功时:
	 * {
        "error" : 0,
        "url" : "http://www.example.com/path/to/a.jpg",
        "message":null
		}
	 * 失败时:
	 * {
        "error" : 1,
        "url" : null,
        "message":"上传失败"
		}
	 * 
	 */
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String uploadPic(MultipartFile uploadFile){
		
		try {
			
			//获取上传文件名称
			String originalFilename = uploadFile.getOriginalFilename();
			//获取文件扩展名  jpg
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			
			//创建fastDFS工具类对象,实现文件上传
			FastDFSClient fClient = new FastDFSClient("classpath:conf/client.conf");
			
			//上传
			//storage_path0=/home/storage_path0
			//M00
			//storage_path1
			//M01
			//返回图片虚拟地址:group1/M00/00/00/wKhCQ1opB9uAb0VHAA1rIuRd3Es992.jpg
			String url = fClient.uploadFile(uploadFile.getBytes(), extName);
			
			//组合图片服务器绝对地址
			url = IMAGE_URL+url;
			
			//上传图片成功
			//创建KindEditorModel对象,封装图片上传信息
			KindEditorModel model = new KindEditorModel();
			//
			model.setError(0);
			model.setUrl(url);
			
			//转换json字符串
			String picJson = JsonUtils.objectToJson(model);

			return picJson;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//上传图片失败
			//创建KindEditorModel对象,封装图片上传信息
			KindEditorModel model = new KindEditorModel();
			//
			model.setError(1);
			model.setMesssage("上传失败");
			//转换json字符串
			String picJson = JsonUtils.objectToJson(model);

			return picJson;
		}
		
		
		
	}

}
