package com.waben.stock.datalayer.manage.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.waben.stock.interfaces.dto.StaffDto;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.core.Converter;
import org.apache.commons.lang.ClassUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/***
 * @author yuyidi 2017-11-15 14:23:01
 * @class com.waben.stock.datalayer.manage.entity.Staff
 * @description 系统员工用户
 */
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", length = 32)
    private String userName;
    @Column
    private String password;

    @JsonIgnore
    @ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JoinTable(name = "staff_role", joinColumns = {@JoinColumn(name = "staff_id")}, inverseJoinColumns = {@JoinColumn
            (name = "role_id")})
    private Set<Role> roles;
    @Column
    private String salt;
    @Column
    private Boolean state;
    @Column
    private Date createTime;
    @Column
    private Date updateTime;
    @Column
    private String loginTime;
    @Column
    private String ip;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getUpdateTime() {

        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
