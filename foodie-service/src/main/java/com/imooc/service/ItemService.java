package com.imooc.service;

import com.imooc.pojo.*;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.ShopcartVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    public Items queryItemById(String id);

    public List<ItemsImg> queryItemImgList(String id);

    public List<ItemsSpec> queryItemSpecList(String itemId);

    public ItemsParam queryItemParam(String itemId);

    public CommentLevelCountsVO queryCommentCounts(String itemId);

    public PagedGridResult queryPagedComments(String itemId, Integer level,
                                              Integer page, Integer pageSize);

    public PagedGridResult searchItems(String keywords, String sort,
                                              Integer page, Integer pageSize);

    public PagedGridResult searchItems(Integer catId, String sort,
                                       Integer page, Integer pageSize);

    public List<ShopcartVO> queryItemsBySpecIds(String specIds);

    public ItemsSpec queryItemSpecById(String specId);

    public String queryItemMainImgById(String itemId);

    public void decreaseItemSpecStock(String specId,int buyCounts);
}
