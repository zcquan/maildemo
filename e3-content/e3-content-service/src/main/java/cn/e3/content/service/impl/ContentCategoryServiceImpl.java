package cn.e3.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3.content.service.ContentCategoryService;
import cn.e3.mapper.TbContentCategoryMapper;
import cn.e3.pojo.TbContentCategory;
import cn.e3.pojo.TbContentCategoryExample;
import cn.e3.pojo.TbContentCategoryExample.Criteria;
import cn.e3.utils.E3mallResult;
import cn.e3.utils.TreeNode;
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	//注入mapper接口代理对象
	@Autowired
	TbContentCategoryMapper contentCategoryMapper;

	/**
	 * 需求:根据父id查询节点下面子节点
	 * 参数:Long parentId
	 * 返回值:List<TreeNode>
	 * 服务是否发布?
	 */
	public List<TreeNode> findContentCategoryTreeNodeList(Long parentId) {
		
		//创建树形节点集合List<TreeNode>,封装属性节点
		List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
		
		// 创建example对象
		TbContentCategoryExample example = new TbContentCategoryExample();
		//创建criteria对象
		Criteria createCriteria = example.createCriteria();
		//设置查询参数:根据父id查询节点下面子节点
		createCriteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> catList = contentCategoryMapper.selectByExample(example);
		//循环分类表集合数据,把分类数据封装到treeNodeList
		for (TbContentCategory tbContentCategory : catList) {
			//创建属性节点对象,封装属性
			TreeNode node = new TreeNode();
			//封装id
			node.setId(tbContentCategory.getId());
			//封装名称
			node.setText(tbContentCategory.getName());
			//封装状态
			//如果is_parent=1,表示此节点有子节点,state="closed" 表示可打开状态
			//如果is_parent=0,表示没有子节点,state="open" 表示已经处于打开状态,没办法再打开
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			
			//把节点对象放入节点集合
			treeNodeList.add(node);
			
		}
		return treeNodeList;
	}

	/**
	 * 需求:新建树形节点
	 * 参数:Long parentId,String name
	 * 返回值:E3mallResult
	 * 业务:
	 * 1,新建节点的父节点如果原来是子节点,修改父节点的状态
	 * 2,新建节点的父节点原来是父节点,直接添加接口即可
	 */
	public E3mallResult createNode(Long parentId, String name) {
		// 创建广告分类对象
		TbContentCategory contentCategory = new TbContentCategory();
		//封装分类属性
		//设置父id
		contentCategory.setParentId(parentId);
		//设置树形节点名称
		contentCategory.setName(name);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		//排列序号，表示同级类目的展现次序，如数值相等则按名称次序排列。取值范围:大于零的整数
		contentCategory.setSortOrder(1);
		//新建节点一定是子节点
		contentCategory.setIsParent(false);
		//节点创建时间
		Date date = new Date();
		contentCategory.setCreated(date);
		contentCategory.setUpdated(date);
		
		//新建节点父parentId,是父节点的id
		//因此可以根据父节点id查询出父节点对象,然后更新 父节点的状态
		TbContentCategory tbContentCategory = contentCategoryMapper.selectByPrimaryKey(parentId);
		//判断父节点的状态是否是子节点
		if(!tbContentCategory.getIsParent()){
			//此时父节点就是子节点,修改节点对象值
			tbContentCategory.setIsParent(true);
			//修改
			contentCategoryMapper.updateByPrimaryKeySelective(tbContentCategory);
		}
		
		
		//保存节点对象
		contentCategoryMapper.insert(contentCategory);
		//返回新建节点对象id
		return E3mallResult.ok(contentCategory);
	}

}
