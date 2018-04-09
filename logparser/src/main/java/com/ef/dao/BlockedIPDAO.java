package com.ef.dao;

import com.ef.model.BlockedIP;
import com.ef.model.Log;

import java.util.List;

public class BlockedIPDAO extends BaseDAO<BlockedIP> {

    @Override
    protected Class<BlockedIP> getEntityClass() {
        return BlockedIP.class;
    }
    
    public void saveBlockedIps(List<BlockedIP> ips) {
        ips.forEach(this::insert);
    }
    
   
}

