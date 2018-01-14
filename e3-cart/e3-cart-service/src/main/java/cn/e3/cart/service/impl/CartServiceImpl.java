package cn.e3.cart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3.cart.service.CartService;
import cn.e3.jedis.dao.JedisDao;
import cn.e3.mapper.TbItemMapper;
import cn.e3.pojo.TbItem;
import cn.e3.pojo.TbItemDesc;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.JsonUtils;
@Service
public class CartServiceImpl implements CartService {
	
	//注入jedisDao
	@Autowired
	private JedisDao jedisDao;
	
	//注入商品mapper接口代理对象
	@Autowired
	private TbItemMapper itemMapper;
	
	
	//注入redis购物车唯一标识
	@Value("${REDIS_CART}")
	private String REDIS_CART;
	
	//注入redis购物车有序集合标识
	@Value("${REDIS_SORT_CART}")
	private String REDIS_SORT_CART;
	
	/**
	 * 需求:添加redis购物车
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return E3mallResult
	 */
	public E3mallResult addRedisCart(Long userId, Long itemId, Integer num) {
		// 判断redis购物车中是否有相同的商品
		Boolean hexists = jedisDao.hexists(REDIS_CART+":"+userId, itemId+"");
		//判断
		if(hexists){
			//存在相同商品
			//获取redis购物车商品
			String itemJson = jedisDao.hget(REDIS_CART+":"+userId, itemId+"");
			//把商品数据转换为商品对象
			TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);			
			//商品数量相加
			item.setNum(item.getNum()+num);
			
			//抽取写入redis购物车方法,且是有序集合
			this.addSortedRedisCart(userId, item);
		}
		
		//如果不存在相同商品
		if(!hexists){
			//直接购买商品  (查询购买商品数据,放入购物车即可)
			TbItem item = itemMapper.selectByPrimaryKey(itemId);
			//设置购买数量
			item.setNum(num);
			
			this.addSortedRedisCart(userId, item);
			
		}
		
		//返回
		return E3mallResult.ok();
	}

	private void addSortedRedisCart(Long userId, TbItem item) {
		// TODO Auto-generated method stub
		//把商品对象写回redis购物车
		jedisDao.hset(REDIS_CART+":"+userId, item.getId()+"", JsonUtils.objectToJson(item));
		//需求: 要求写回购物车商品具有顺序,先进后出, 后添加的先查询..
		//获取当前时间,毫秒
		Long currentTimeMillis = System.currentTimeMillis();
		jedisDao.zadd(REDIS_SORT_CART+":"+userId, currentTimeMillis.doubleValue(), item.getId()+"");
	}

	/**
	 * 需求:合并cookie购物车到redis购物车
	 * @param userId
	 * @param cartList
	 * @return E3mallResult
	 */
	public E3mallResult mergeRedisCart(Long userId, List<TbItem> cartList) {
		// 循环购物车列表
		for (TbItem tbItem : cartList) {
			this.addRedisCart(userId, tbItem.getId(), tbItem.getNum());
		}
		return E3mallResult.ok();
	}

	/**
	 * 需求:根据用户id查询此用户的购物车列表数据
	 * @param userId
	 * @return List<TbItem>
	 * 业务:
	 * 后添加先展示,要求查询商品列表数据具有顺序性.
	 * 1,查询sorted-set有序集合中商品id查询出来
	 * 2,根据有序商品id查询hash购物车数据
	 */
	public List<TbItem> findRedisSortedCart(Long userId) {
		//创建集合对象,封装购物车数据
		List<TbItem> cartList = new ArrayList<TbItem>();
		
		// 1,查询sorted-set有序集合中商品id查询出来
		Set<String> itemIds = jedisDao.zrevrange(REDIS_SORT_CART+":"+userId, 0L,-1L);
		// 2,循环集合,获取有序商品id
		for (String itemId : itemIds) {
			
		//3,根据有序商品id查询hash购物车数据	
		String itemJson = jedisDao.hget(REDIS_CART+":"+userId, itemId);
		//把商品数据转换成对象
		TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);
		
		//把商品对象放入集合
		cartList.add(item);
		}
		return cartList;
	}

	/**
	 * 需求:根据用户id,删除此用户下面的itemid所表示的这个商品
	 * @param userId
	 * @param itemId
	 * @return E3mallResult
	 * 业务:
	 * 1,先删除排序商品id
	 * 2,再删除商品数据
	 */
	public E3mallResult deleteRedisCart(Long userId, Long itemId) {
		//1,先删除排序商品id
		jedisDao.zrem(REDIS_SORT_CART+":"+userId,itemId+"");
		//2,再删除商品数据
		jedisDao.hdel(REDIS_CART+":"+userId, itemId+"");
		return E3mallResult.ok();
	}

	/**
	 * 需求:修改redis购物车商品数量
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return
	 */
	public E3mallResult updateRedisCart(Long userId, Long itemId, Integer num) {
		// 获取购物车中商品对象
		String itemJson = jedisDao.hget(REDIS_CART+":"+userId, itemId+"");
		//把json商品数据转换成一个商品对象
		TbItem item = JsonUtils.jsonToPojo(itemJson, TbItem.class);
		//设置购物车数量
		item.setNum(num);
		
		//写回redis购物车
		jedisDao.hset(REDIS_CART+":"+userId, itemId+"", JsonUtils.objectToJson(item));
		
		
		return E3mallResult.ok();
	}

}
