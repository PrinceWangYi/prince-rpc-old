package com.prince.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.prince.config.RegistryConfig;
import com.prince.model.ServiceMetaInfo;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    private final Set<String> localCache = new HashSet<>();

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建30s的租约
        long leaseId = client.getLeaseClient().grant(30).get().getID();

        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        // 将键值对和租约关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();

        kvClient.put(getByteSequence(registerKey), getByteSequence(JSONUtil.toJsonStr(serviceMetaInfo)), putOption).get();
        // 加入本地缓存
        localCache.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(getByteSequence(registerKey));
        localCache.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 前缀搜索，末尾要加“/”
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            GetOption option = GetOption.builder().isPrefix(true).build();

            List<KeyValue> kvs = kvClient.get(getByteSequence(searchPrefix), option).get().getKvs();
            return kvs.stream().map(kv -> JSONUtil.toBean(kv.getValue().toString(), ServiceMetaInfo.class))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            System.out.println("心跳检测" + System.currentTimeMillis());
            for (String key : localCache) {
                try {
                    List<KeyValue> keyValues = kvClient.get(getByteSequence(key)).get().getKvs();

                    // 节点已过期
                    if (CollUtil.isEmpty(keyValues)) {
                        continue;
                    }

                    for (KeyValue keyValue : keyValues) {
                        String value = keyValue.getValue().toString();
                        System.out.println("etcd key value" + value);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(key +  "续签失败",e);
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {

    }

    @Override
    public void destroy() {
        System.out.println("服务器节点下线");
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }

    private ByteSequence getByteSequence(String key) {
        return ByteSequence.from(key, StandardCharsets.UTF_8);
    }

}
