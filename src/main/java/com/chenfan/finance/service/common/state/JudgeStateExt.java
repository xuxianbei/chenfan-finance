package com.chenfan.finance.service.common.state;

/**
 * 额外状态判断
 *
 * @author: xuxianbei
 * Date: 2021/5/19
 * Time: 16:15
 * Version:V1.0
 */
@FunctionalInterface
public interface JudgeStateExt {

    /**
     * 执行，判断状态，返回true，表示状态可以跳转  目前主要应用在批量导入操作
     * 不过没啥用处，实际代码还是写崩了，之所以要写成这样，需要维持整体编程思想
     *
     * @param oldState
     * @param newState
     * @return
     */
    boolean apply(int oldState, int newState);
}
