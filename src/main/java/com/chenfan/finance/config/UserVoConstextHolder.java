package com.chenfan.finance.config;

import com.chenfan.common.vo.UserVO;

/**
 * Created by mbji on 2018/11/14.
 * @author mbji
 * @date 2018/11/14.
 */
public class UserVoConstextHolder {
    private static final ThreadLocal<UserVO> USER_CONTEXT_HOLDER = new ThreadLocal();

    private UserVoConstextHolder() {
    }

    public static UserVO getUserVo() {
        return (UserVO) USER_CONTEXT_HOLDER.get();
    }

    public static void setUserVo(UserVO userVo) {
        USER_CONTEXT_HOLDER.set(userVo);
    }

    public static void remove() {
        USER_CONTEXT_HOLDER.remove();
    }
}
