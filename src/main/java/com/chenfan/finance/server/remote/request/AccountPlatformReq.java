package com.chenfan.finance.server.remote.request;


import com.chenfan.finance.server.remote.model.ThirdPayAccount;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AccountPlatformReq implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ThirdPayAccount> thirdPayAccounts;
}
