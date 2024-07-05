package com.prince.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.prince.config.RegistryConfig;
import com.prince.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    /**
     * 已注册服务，键的缓存
     */
    private final Set<String> localCache = new HashSet<>();

    /**
     * 发现服务缓存
     */
    private final RegistryCache serviceCache = new RegistryCache();

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     *
     */
    private final Set<String> watchKeySet = new ConcurrentHashSet<>();

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
        List<ServiceMetaInfo> cacheList = serviceCache.readCache();
        if (cacheList != null) {
            return cacheList;
        }
        // 前缀搜索，末尾要加“/”
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            GetOption option = GetOption.builder().isPrefix(true).build();

            List<KeyValue> kvs = kvClient.get(getByteSequence(searchPrefix), option).get().getKvs();
            List<ServiceMetaInfo> serviceMetaInfoList = kvs.stream()
                    .map(kv -> {
                        watch(kv.getKey().toString());
                        return JSONUtil.toBean(kv.getValue().toString(), ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
            serviceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
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
        Watch watchClient = client.getWatchClient();
        if (watchKeySet.add(serviceNodeKey)) {
            watchClient.watch(getByteSequence(ETCD_ROOT_PATH + serviceNodeKey), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            serviceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
                serviceCache.clearCache();
            });
        }
    }

    @Override
    public void destroy() {
        System.out.println("服务器节点下线");

        for (String key : localCache) {
            try {
                kvClient.delete(getByteSequence(key)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败", e);
            }
        }

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
