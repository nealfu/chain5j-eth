package com.xwc1125.chain5j.protocol.core.methods.request;

import java.util.Arrays;
import java.util.List;

import com.xwc1125.chain5j.protocol.core.DefaultBlockParameter;
import jnr.constants.platform.NameInfo;

/**
 * Filter implementation as per
 * <a href="https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_newfilter">docs</a>.
 */
public class EthFilter extends Filter<EthFilter> {
    private DefaultBlockParameter fromBlock;  // optional, params - defaults to latest for both
    private DefaultBlockParameter toBlock;
    private List<String> address;  // spec. implies this can be single address as string or list

    public EthFilter() {
        super();
    }

    public EthFilter(DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock,
                     List<String> address) {
        super();
        this.fromBlock = fromBlock;
        this.toBlock = toBlock;
        this.address = address;
    }

    public EthFilter(DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock,
                     String address) {
        this(fromBlock, toBlock, Arrays.asList(address));
    }

    public DefaultBlockParameter getFromBlock() {
        return fromBlock;
    }

    public DefaultBlockParameter getToBlock() {
        return toBlock;
    }

    public String getAddress() {
        if (address != null && address.size() > 1) {
            return address.toString();
        }
        return address.get(0);
    }

    @Override
    EthFilter getThis() {
        return this;
    }
}
