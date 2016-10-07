package net.dearcode.candy.controller.component;

import net.dearcode.candy.model.ServiceResponse;

/**
 *  * Created by c-wind on 2016/9/30 13:28
 *  * mail：root@codecn.org
 *  
 */
public abstract class RPC {
    public abstract ServiceResponse getResponse() throws Exception;

    public ServiceResponse Call() {
        ServiceResponse sr = null;
        try {
            sr = getResponse();
        } catch (Exception e) {
            sr.setError(e.getMessage());
        }
        return sr;
    }
}
