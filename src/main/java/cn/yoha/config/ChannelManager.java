package cn.yoha.config;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理所有的channel，通过 terminalPhone -> channelId -> channel
 * 可以处理比如向特定的终端发送消息、广播等功能
 */
@Slf4j
@Component
public class ChannelManager {

    private static final AttributeKey<String> TERMINAL_PHONE = AttributeKey.newInstance("terminalPhone");

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static Map<String, ChannelId> channelMap = new ConcurrentHashMap<>();

    public boolean add(String terminalPhone, Channel channel) {
        boolean add = channels.add(channel);
        if (add) {
            channel.attr(TERMINAL_PHONE).set(terminalPhone);
            channel.closeFuture().addListener((ChannelFutureListener) future ->
                    channelMap.remove(future.channel().attr(TERMINAL_PHONE).get()));
            channelMap.put(terminalPhone, channel.id());
        }
        return add;
    }

    public boolean remove(String terminalPhone) {
        return channels.remove(channelMap.remove(terminalPhone));
    }

    public Channel get(String terminalPhone) {
        return channels.find(channelMap.get(terminalPhone));
    }
}
