package cn.e3.user.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3.jedis.dao.JedisDao;
import cn.e3.mapper.TbUserMapper;
import cn.e3.pojo.TbUser;
import cn.e3.pojo.TbUserExample;
import cn.e3.pojo.TbUserExample.Criteria;
import cn.e3.user.service.UserService;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;
@Service
public class UserServiceImpl implements UserService {

	//注入用户mapper接口代理对象
	@Autowired
	private TbUserMapper userMapper;
	
	//注入用户身份信息唯一标识
	@Value("${SESSION_KEY}")
	private String SESSION_KEY;
	
	//注入jedisDao
	@Autowired
	private JedisDao jedisDao;
	
	//注入用户身份信息过期时间
	@Value("${SESSION_KEY_EXPIRE_TIME}")
	private Integer SESSION_KEY_EXPIRE_TIME;
	
	/**
	 * 需求:用户注册数据校验
	 * 1,校验用户是否被占有 type=1
	 * 2,邮箱是否被注册 type=3
	 * 3,校验手机号  type=2
	 * 参数:String param,Integer type
	 * 返回值:E3mallResult
	 * 返回数据，true：数据可用，false：数据不可用
	 */
	public E3mallResult checkData(String param, Integer type) {
		// 创建example对象
		TbUserExample example = new TbUserExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数
		if(type==1){
			//校验用户名
			createCriteria.andUsernameEqualTo(param);
		}else if(type==2){
			//校验手机号
			createCriteria.andPhoneEqualTo(param);
		}else if(type==3){
			//邮箱
			createCriteria.andEmailEqualTo(param);
		}
		//执行查询
		List<TbUser> list = userMapper.selectByExample(example);
		//判断集合数据是否为空
		if(list.isEmpty()){
			//返回数据可用
			return E3mallResult.ok(true);
		}
		//数据不可用
		return E3mallResult.ok(false);
	}

	/**
	 * 需求:用户注册
	 * 参数:TbUser user
	 * 返回值:E3mallResult
	 * 成功时:
	 * {
		status: 200
		msg: "注册成功"
		data: null
	   }
	 * 失败时:
	 * {
		status: 400
		msg: "注册失败. 请校验数据后请再提交数据."
		data: null
	   }

	 */
	public E3mallResult register(TbUser user) {
		try {
			// 密码加密
			String password = user.getPassword();
			//给密码加载
			String newPwd = DigestUtils.md5DigestAsHex(password.getBytes());
			//把加密密码设置用户对象
			user.setPassword(newPwd);
			//补全时间
			Date date = new Date();
			user.setCreated(date);
			user.setUpdated(date);
			//保存
			userMapper.insertSelective(user);
			
			//成功
			return E3mallResult.ok();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			//注册失败
			return E3mallResult.build(400, "注册失败. 请校验数据后请再提交数据.", null);
		}
		
	}

	/**
	 * 需求:用户登录
	 * 参数:String username,String password
	 * 返回值:E3mallResult.ok(token)
	 * 业务流程:
	 * 1,接受用户名和密码
	 * 2,根据用户名查询用户数据库,判断用户是否存在
	 * 3,如果用户不存在,返回 "用户名或密码错误"
	 * 4,如果用户存在,判断密码是否匹配(密码必须加密后再进行匹配)
	 * 5,如果密码不匹配,返回  "用户名或密码错误"
	 * 6,如果密码匹配成功,登录成功
	 * 7,生成用户身份信息唯一标识 token
	 * 8,把用户身份信息写入redis
	 * 9,返回token
	 * 共享session:使用redis服务器
	 * 数据结构:String
	 * key:SESSION_KEY:token
	 * value:json()
	 * 用户身份信息过期时间:
	 * 12小时=43200s
	 * 
	 */
	public E3mallResult login(String username, String password) {
		//创建example对象
		TbUserExample example = new TbUserExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();		
		// 根据用户名查询用户数据库
		createCriteria.andUsernameEqualTo(username);
		//执行查询
		List<TbUser> uList = userMapper.selectByExample(example);
		//判断用户是否存在
		if(uList.isEmpty()){
			//不存在
			return E3mallResult.build(201, "用户名或密码错误");
		}
		//获取用户数据信息
		TbUser user = uList.get(0);
		//匹配密码,验证密码是否相等
		//获取旧的密码
		String oldPwd = user.getPassword();
		//新密码加密
		String newPwd = DigestUtils.md5DigestAsHex(password.getBytes());
		//判断密码是否相等
		if(!oldPwd.equals(newPwd)){
			return E3mallResult.build(201, "用户名或密码错误");
		}
		
		//登录成功
		//生成uuid
		String token = UUID.randomUUID().toString();
		
		//把用户身份信息写入redis服务器
		//添加session
		jedisDao.set(SESSION_KEY+":"+token, JsonUtils.objectToJson(user));
		//用户身份信息过期时间
		jedisDao.expire(SESSION_KEY+":"+token, SESSION_KEY_EXPIRE_TIME);
		
		//返回token
		return E3mallResult.ok(token);
	}

	/**
	 * 需求:根据token查询用户身份信息
	 * 参数:String token
	 * 返回值:E3mallResult.ok(User)
	 * {
		status: 200
		msg: "OK"
		data: "{"id":1,"username":"zhangzhijun","phone":"15800807944",
		"email":"420840806@qq.com","created":1414119176000,"updated":1414119179000}"
	   }
	  业务:
	 1,从redis服务器查询用户身份信息
	 2,过期时间重置
	 */
	public E3mallResult findUserByToken(String token) {
		// 根据token查询redis服务器
		String userJson = jedisDao.get(SESSION_KEY+":"+token);
		//把json格式转换成对象
		TbUser user = null;		
		//判断用户身份信息是否存在
		if(StringUtils.isNotBlank(userJson)){
			
			user = JsonUtils.jsonToPojo(userJson, TbUser.class);
			//重置过期时间
			jedisDao.expire(SESSION_KEY+":"+token, SESSION_KEY_EXPIRE_TIME);
		}
		//返回
		return E3mallResult.ok(user);
	}

}
