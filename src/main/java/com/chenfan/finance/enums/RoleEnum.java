package com.chenfan.finance.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mbji
 *         <p>
 *         此枚举把当前角色跟下个任务人,可操作的单据状态，通过后单据要修改的状态绑定了一下
 *         用于付款申请
 */
public enum RoleEnum {
    /**
     * 当前角色id     ，下个任务人id        ，当前角色名称   ，  可操作状态            ，    操作后状态          ，上个操作人id
     */
    FIRST_OM("224", new Integer[]{221}, "业务一组组长", AdvencePayEnum.NOT_CONFIRM, AdvencePayEnum.CONFIRM, new String[]{"224"}),

    FIRST_GM("221", new Integer[]{203}, "业务主管", AdvencePayEnum.CONFIRM, AdvencePayEnum.AUDIT, new String[]{"224"}),

    SUPPLYCHAIN_INTERN("203", new Integer[]{111}, "供应链实习助理", AdvencePayEnum.AUDIT, AdvencePayEnum.SUBMIT, new String[]{"221", "222", "223","227","231","234"}),
    FINANCE("111", new Integer[]{116}, "财务审核员", AdvencePayEnum.SUBMIT, AdvencePayEnum.FINANCE_AUDIT, new String[]{"203"}),
    FINANCE_GM("116", new Integer[]{105}, "财务组长", AdvencePayEnum.FINANCE_AUDIT, AdvencePayEnum.COMPLETE, new String[]{"111"}),
    CASHIER("105", new Integer[]{0}, "出纳", AdvencePayEnum.COMPLETE, AdvencePayEnum.PAID, new String[]{"116"});
    /**
     * 角色id
     *
     */
    private String code;
    /**
     * 下个任务人
     */
    private Integer[] nextTaskPerson;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 可操作的状态
     */
    private AdvencePayEnum state;
    /**
     * 通过状态
     */
    private AdvencePayEnum agreeState;

    private String[] lastTaskPerson;

    RoleEnum(String code, Integer[] nextTaskPerson, String roleName, AdvencePayEnum state, AdvencePayEnum agreeState, String[] lastTaskPerson) {
        this.code = code;
        this.nextTaskPerson = nextTaskPerson;
        this.roleName = roleName;
        this.state = state;
        this.agreeState = agreeState;
        this.lastTaskPerson = lastTaskPerson;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer[] getNextTaskPerson() {
        return nextTaskPerson;
    }

    public void setNextTaskPerson(Integer[] nextTaskPerson) {
        this.nextTaskPerson = nextTaskPerson;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public AdvencePayEnum getState() {
        return state;
    }

    public void setState(AdvencePayEnum state) {
        this.state = state;
    }

    public AdvencePayEnum getAgreeState() {
        return agreeState;
    }

    public void setAgreeState(AdvencePayEnum agreeState) {
        this.agreeState = agreeState;
    }

    public String[] getLastTaskPerson() {
        return lastTaskPerson;
    }

    public void setLastTaskPerson(String[] lastTaskPerson) {
        this.lastTaskPerson = lastTaskPerson;
    }

    public static RoleEnum getEnumByCode(String code) {
        for (RoleEnum value : RoleEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static List<RoleEnum> getEnumsByState(AdvencePayEnum state, String roleCode) {
        List<RoleEnum> roleEnums = new ArrayList<>();
        for (RoleEnum value : RoleEnum.values()) {
            if (value.getState().equals(state) && Arrays.asList(value.getLastTaskPerson()).contains(roleCode)) {
                roleEnums.add(value);
            }
        }
        return roleEnums;
    }
}
