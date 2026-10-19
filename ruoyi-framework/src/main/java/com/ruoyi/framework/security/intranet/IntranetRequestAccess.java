package com.ruoyi.framework.security.intranet;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 工作流服务间调用的内网信任边界。
 *
 * 仅使用 TCP 对端地址，不信任可伪造的 X-Forwarded-For。若应用位于反向代理后，
 * 仍必须在网关/安全组中禁止公网访问这些路由。
 */
@Component
public class IntranetRequestAccess
{
    private final boolean enabled;
    private final List<CidrBlock> allowedCidrs;

    public IntranetRequestAccess(
            @Value("${crm.workflow.intranet.enabled:true}") boolean enabled,
            @Value("${crm.workflow.intranet.allowed-cidrs:127.0.0.1/32,::1/128,10.0.0.0/8,172.16.0.0/12,192.168.0.0/16}") String cidrs)
    {
        this.enabled = enabled;
        this.allowedCidrs = parseCidrs(cidrs);
    }

    public boolean isTrusted(HttpServletRequest request)
    {
        if (!enabled || request == null || StringUtils.isBlank(request.getRemoteAddr()))
        {
            return false;
        }
        try
        {
            InetAddress remote = InetAddress.getByName(request.getRemoteAddr());
            for (CidrBlock block : allowedCidrs)
            {
                if (block.contains(remote))
                {
                    return true;
                }
            }
            return false;
        }
        catch (UnknownHostException e)
        {
            return false;
        }
    }

    public void requireTrusted(HttpServletRequest request)
    {
        if (!isTrusted(request))
        {
            throw new ServiceException("Forbidden: intranet access only", HttpStatus.FORBIDDEN);
        }
    }

    private static List<CidrBlock> parseCidrs(String configured)
    {
        List<CidrBlock> blocks = new ArrayList<>();
        if (StringUtils.isBlank(configured))
        {
            return blocks;
        }
        for (String item : configured.split(","))
        {
            String value = item.trim();
            if (!value.isEmpty())
            {
                blocks.add(CidrBlock.parse(value));
            }
        }
        return blocks;
    }

    private static final class CidrBlock
    {
        private final byte[] network;
        private final int prefixLength;

        private CidrBlock(byte[] network, int prefixLength)
        {
            this.network = network;
            this.prefixLength = prefixLength;
        }

        private static CidrBlock parse(String value)
        {
            String[] parts = value.split("/", 2);
            try
            {
                byte[] address = InetAddress.getByName(parts[0]).getAddress();
                int maxBits = address.length * 8;
                int prefix = parts.length == 2 ? Integer.parseInt(parts[1]) : maxBits;
                if (prefix < 0 || prefix > maxBits)
                {
                    throw new IllegalArgumentException("Invalid CIDR prefix: " + value);
                }
                return new CidrBlock(address, prefix);
            }
            catch (UnknownHostException | NumberFormatException e)
            {
                throw new IllegalArgumentException("Invalid CIDR: " + value, e);
            }
        }

        private boolean contains(InetAddress address)
        {
            byte[] candidate = address.getAddress();
            if (candidate.length != network.length)
            {
                return false;
            }
            int fullBytes = prefixLength / 8;
            int remainingBits = prefixLength % 8;
            for (int i = 0; i < fullBytes; i++)
            {
                if (candidate[i] != network[i])
                {
                    return false;
                }
            }
            if (remainingBits == 0)
            {
                return true;
            }
            int mask = (0xFF << (8 - remainingBits)) & 0xFF;
            return (candidate[fullBytes] & mask) == (network[fullBytes] & mask);
        }
    }
}
