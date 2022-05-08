package server;

import com.alibaba.fastjson.JSON;
import common.RpcInvocation;
import common.RpcProtocol;
import common.cache.CommonServerCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author 11105157
 * @Description 服务端接收数据之后的处理器，当数据抵达这个位置的时候，已经是以RpcProtocol的格式展现了
 * @Date 2022/4/9
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InvocationTargetException, IllegalAccessException {
        //服务端接收数据的时候统一以RpcProtocol协议的格式接收，具体的发送逻辑见文章下方客户端发送部分
        RpcProtocol rpcProtocol = (RpcProtocol) msg;
        String json = new String(rpcProtocol.getContent(), 0, rpcProtocol.getContentLength());
        RpcInvocation reqRpcInvocation = JSON.parseObject(json, RpcInvocation.class);
        //这里的PROVIDER_CLASS_MAP就是一开始预先在启动时候存储的Bean集合
        Object aimObject = CommonServerCache.PROVIDER_CLASS_MAP.get(reqRpcInvocation.getTargetServiceName());
        Method[] methods = aimObject.getClass().getDeclaredMethods();
        Object result = null;
        for (Method method : methods) {
            if (method.getName().equals(reqRpcInvocation.getTargetMethod())) {
                // 通过反射找到目标对象，然后执行目标方法并返回对应值
                if (method.getReturnType().equals(Void.TYPE)) {
                    method.invoke(aimObject, reqRpcInvocation.getArgs());
                } else {
                    result = method.invoke(aimObject, reqRpcInvocation.getArgs());
                }
                break;
            }
        }
        reqRpcInvocation.setResponse(result);
        // 将执行的结果放到请求rpcInvocation中，最终包装成respRpcProtocol
        RpcProtocol respRpcProtocol = new RpcProtocol(JSON.toJSONString(reqRpcInvocation).getBytes());
        ctx.writeAndFlush(respRpcProtocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
