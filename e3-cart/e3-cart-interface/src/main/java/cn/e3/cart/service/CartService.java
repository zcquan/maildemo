package cn.e3.cart.service;

import java.util.List;

import cn.e3.pojo.TbItem;
import cn.e3.utils.E3mallResult;

public interface CartService {
	/**
	 * 需求:添加redis购物车
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return E3mallResult
	 */
	E3mallResult addRedisCart(Long userId, Long itemId, Integer num);
	/**
	 * 需求:合并cookie购物车到redis购物车
	 * @param userId
	 * @param cartList
	 * @return E3mallResult
	 */
	E3mallResult mergeRedisCart(Long userId, List<TbItem> cartList);
	/**
	 * 需求:根据用户id查询此用户的购物车列表数据
	 * @param userId
	 * @return List<TbItem>
	 */
	List<TbItem> findRedisSortedCart(Long userId);
	/**
	 * 需求:根据用户id,删除此用户下面的itemid所表示的这个商品
	 * @param userId
	 * @param itemId
	 * @return
	 */
	E3mallResult deleteRedisCart(Long userId, Long itemId);
	/**
	 * 需求:修改redis购物车商品数量
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return
	 */
	E3mallResult updateRedisCart(Long userId, Long itemId, Integer num);

}
