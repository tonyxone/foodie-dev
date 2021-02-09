package com.imooc.service;

import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    public OrderVO createOrder(List<ShopcartBO> shopcartList,SubmitOrderBO submitOrderBO);

    public void updateOrderStatus(String orderId,Integer orderStatus);

    public OrderStatus queryOrderStatusInfo(String orderId);

    /*
    * 关闭超市未支付订单
    * */
    public void closeOrder();
}
