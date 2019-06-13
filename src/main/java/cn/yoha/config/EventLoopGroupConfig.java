package cn.yoha.config;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventLoopGroupConfig {
    @Value("${netty.threads.boss}")
    private int bossThreadNum;
    @Value("${netty.threads.worker}")
    private int workerThreadNum;
    @Value("${netty.threads.business}")
    private int businessThreadNum;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public EventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossThreadNum);
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public EventLoopGroup workerGroup() {
        return new NioEventLoopGroup(workerThreadNum);
    }

    @Bean(name = "businessGroup", destroyMethod = "shutdownGracefully")
    public EventExecutorGroup businessGroup() {
        return new DefaultEventExecutorGroup(businessThreadNum);
    }
}
