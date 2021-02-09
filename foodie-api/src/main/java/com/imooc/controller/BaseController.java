package com.imooc.controller;

import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.RedisOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.UUID;

public class BaseController {

    public static final Integer COMMENT_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 20;

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMON_PAGE_SIZE = 10;


    public static final String REDIS_USER_TOKEN = "redis_user_token";


    @Autowired
    private RedisOperator redisOperator;

    //微信支付成功 -> 支付中心 -> 天天吃货平台
    //todo 内网穿透
    String payReturnUrl = "http://192.168.0.29:8088/foodie-dev-api/orders/notifyMerchantOrderPaid";

    //支付中心调用地址
    String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    //用户上传头像地址
    public static final String IMAGE_USER_FACE_LOCATION = File.separator + "Workspaces"
                   + File.separator + "images"
                   + File.separator + "foodie"
                   + File.separator + "faces";

    @Autowired
    public MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     * @return
     */
    public IMOOCJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return IMOOCJSONResult.errorMsg("订单不存在！");
        }
        return IMOOCJSONResult.ok(order);
    }

    public UsersVO conventUsersVO(Users users){
        // 实现用户redis绘话
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(),uniqueToken);

        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);

        return usersVO;
    }

}
