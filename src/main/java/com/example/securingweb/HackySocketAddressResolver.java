package com.example.securingweb;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.DnsResolver;
import io.lettuce.core.resource.SocketAddressResolver;
import org.apache.commons.lang3.tuple.Pair;

public class HackySocketAddressResolver extends SocketAddressResolver {
  private final DnsResolver dnsResolver;
  private static int portIndex = 0;
  private final List<Pair<String, Integer>> hostsPorts;

  public HackySocketAddressResolver(DnsResolver resolver, List<Pair<String, Integer>> hostsPorts) {
    super(resolver);
    dnsResolver = resolver;
    this.hostsPorts = hostsPorts;
  }

  @SuppressWarnings("deprecation")
  public SocketAddress resolve(RedisURI redisURI) {
    if (redisURI.getSocket() != null) {
      return redisURI.getResolvedAddress();
    }

    Pair<String, Integer> hostPort = hostsPorts.get(portIndex % hostsPorts.size());
    portIndex++;

    System.err.printf("HackySocketAddressResolver selected %s:%d\n", hostPort.getLeft(),
        hostPort.getRight());

    return InetSocketAddress.createUnresolved(hostPort.getLeft(), hostPort.getRight());
  }
}
