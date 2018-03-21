package com.centit.framework.cloud;

import com.centit.framework.common.ResponseJSON;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.staticsystem.po.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudPlatformEnvironment implements PlatformEnvironment {

    @Autowired
    RestTemplate restTemplate;

    private static String FRAMEWORK_SERVER_URL="http://SERVICE-FRAMEWORK/platform";
    private Logger logger = LoggerFactory.getLogger(CloudPlatformEnvironment.class);

    private String topOptId;

    public CloudPlatformEnvironment() {
        topOptId = "mainframe";
    }

    /**
     * 刷新数据字典
     *
     * @return 是否刷新
     */
    @Override
    @CacheEvict(value ={
            "DataDictionary","OptInfo","RoleInfo","UserInfo","UnitInfo",
            "UnitUsers","UserUnits","AllUserUnits"},allEntries = true)
    public boolean reloadDictionary() {
        return true;
    }


    @HystrixCommand(fallbackMethod = "dummyListAllRolePower")
    public List<RolePower>  listAllRolePower(){
        ResponseJSON responseJSON =
            restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/allrolepowers/"+topOptId,
                    ResponseJSON.class);
        return responseJSON.getDataAsArray(RolePower.class);
    }

    public List<RolePower>  dummyListAllRolePower(){
        return null;
    }

    @HystrixCommand(fallbackMethod = "dummyListAllOptMethod")
    public List<OptMethod> listAllOptMethod(){
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/alloptmethods/"+topOptId,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(OptMethod.class);
    }

    public List<OptMethod>  dummyListAllOptMethod(){
        return null;
    }

    /**
     * 刷新权限相关的元数据
     *
     * @return 是否刷新
     */
    @Override
    public boolean reloadSecurityMetadata() {
        //这个要定时刷新 或者 通过集成平台来主动刷新
        CentitSecurityMetadata.optMethodRoleMap.clear();
        List<RolePower> rplist = listAllRolePower();
        if(rplist==null || rplist.size()==0)
            return false;
        for(RolePower rp: rplist ){
            List<ConfigAttribute/*roleCode*/> roles = CentitSecurityMetadata.optMethodRoleMap.get(rp.getOptCode());
            if(roles == null){
                roles = new ArrayList<ConfigAttribute/*roleCode*/>();
            }
            roles.add(new SecurityConfig(CentitSecurityMetadata.ROLE_PREFIX + StringUtils.trim(rp.getRoleCode())));
            CentitSecurityMetadata.optMethodRoleMap.put(rp.getOptCode(), roles);
        }
        //将操作和角色对应关系中的角色排序，便于权限判断中的比较
        CentitSecurityMetadata.sortOptMethodRoleMap();
        Map<String, OptInfo> optRepo = getOptInfoRepo();
        List<OptMethod> oulist = listAllOptMethod();
        CentitSecurityMetadata.optTreeNode.setChildList(null);
        CentitSecurityMetadata.optTreeNode.setOptCode(null);
        for(OptMethod ou:oulist){
            OptInfo oi = optRepo.get(ou.getOptId());
            if(oi!=null){
                String  optDefUrl = oi.getOptUrl()+ou.getOptUrl();
                List<List<String>> sOpt = CentitSecurityMetadata.parseUrl(
                        optDefUrl,ou.getOptReq());

                for(List<String> surls : sOpt){
                    OptTreeNode opt = CentitSecurityMetadata.optTreeNode;
                    for(String surl : surls)
                        opt = opt.setChildPath(surl);
                    opt.setOptCode(ou.getOptCode());
                }
            }
        }
        //CentitSecurityMetadata.optTreeNode.printTreeNode();
        return true;
    }

    /**
     * 获取用户所有菜单功能
     *
     * @param userCode userCode
     * @param asAdmin  是否是作为管理员
     * @return List 用户所有菜单功能
     */
    @Override
    public List<OptInfo> listUserMenuOptInfos(String userCode, boolean asAdmin) {
        return listUserMenuOptInfosUnderSuperOptId(userCode,topOptId,asAdmin);
    }

    /**
     * 获取用户所有菜单功能
     *
     * @param userCode   userCode
     * @param superOptId superOptId
     * @param asAdmin    是否是作为管理员
     * @return List 用户所有菜单功能
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListUserMenuOptInfosUnderSuperOptId")
    public List<OptInfo> listUserMenuOptInfosUnderSuperOptId(String userCode, String superOptId, boolean asAdmin) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/usermenu/"+superOptId+"/"+userCode+"?asAdmin="+asAdmin,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(OptInfo.class);

    }

    public List<OptInfo> dummyListUserMenuOptInfosUnderSuperOptId(String userCode, String superOptId, boolean asAdmin) {
        return null;
    }

    /**
     * 获取用户所有角色
     *
     * @param userCode 用户代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IRoleInfo> listUserRolesByUserCode(String userCode) {
        return null;
    }

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IUserInfo> listRoleUserByRoleCode(String roleCode) {
        return null;
    }

    /**
     * 获取用户所有角色
     *
     * @param userCode 用户代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IUserRole> listUserRoles(String userCode) {
        return null;
    }

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IUserRole> listRoleUsers(String roleCode) {
        return null;
    }

    /**
     * 获取用户所有角色
     *
     * @param unitCode 机构代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IUnitRole> listUnitRoles(String unitCode) {
        return null;
    }

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    @Override
    public List<? extends IUnitRole> listRoleUnits(String roleCode) {
        return null;
    }

    /**
     * 根据用户代码获取用户信息，
     *
     * @param userCode userCode
     * @return 用户信息
     */
    @Override
    public IUserInfo getUserInfoByUserCode(String userCode) {
        return null;
    }

    /**
     * 根据登录名获取用户信息，
     *
     * @param loginName loginName
     * @return 登录名获取用户信息
     */
    @Override
    public IUserInfo getUserInfoByLoginName(String loginName) {
        return null;
    }

    /**
     * 根据用户代码获取用户信息，
     *
     * @param unitCode unitCode
     * @return 用户信息
     */
    @Override
    public IUnitInfo getUnitInfoByUnitCode(String unitCode) {
        return null;
    }

    /**
     * 修改用户密码
     *
     * @param userCode     userCode
     * @param userPassword userPassword
     */
    @Override
    public void changeUserPassword(String userCode, String userPassword) {

    }

    /**
     * 验证用户密码
     *
     * @param userCode     userCode
     * @param userPassword userPassword
     * @return 验证结果
     */
    @Override
    public boolean checkUserPassword(String userCode, String userPassword) {
        return false;
    }

    /**
     * 获取所有用户，
     *
     * @return List 所有用户
     */
    @Override
    //@Cacheable(value = "UserInfo",key = "'userList'" )
    //@HystrixCommand(fallbackMethod = "dummyListAllUsers")
    public List<UserInfo> listAllUsers() {

        String jsonString =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/allusers/"+topOptId,
                        String.class);
        ResponseJSON responseJSON = ResponseJSON.valueOfJson(jsonString);
        return responseJSON.getDataAsArray(UserInfo.class);

    }

    public List<UserInfo> dummyListAllUsers() {
        return null;
    }

    /**
     * 获取所有机构
     *
     * @return List 所有机构
     */
    @Override
    public List<? extends IUnitInfo> listAllUnits() {
        return null;
    }

    /**
     * 获取所有用户和机构关联关系
     *
     * @return List 所有用户和机构关联关系
     */
    @Override
    public List<? extends IUserUnit> listAllUserUnits() {
        return null;
    }

    /**
     * 根据用户代码获得 用户所有的机构信息
     *
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    @Override
    public List<? extends IUserUnit> listUserUnits(String userCode) {
        return null;
    }

    /**
     * 根据机构代码获得 机构所有用户信息
     *
     * @param unitCode unitCode
     * @return List 机构所有用户信息
     */
    @Override
    public List<? extends IUserUnit> listUnitUsers(String unitCode) {
        return null;
    }

    /**
     * 获取机构代码映射表
     *
     * @return Map 机构代码映射表
     */
    @Override
    public Map<String, ? extends IUnitInfo> getUnitRepo() {
        return null;
    }

    /**
     * 获取部门编码映射表
     *
     * @return map 部门编码映射表
     */
    @Override
    public Map<String, ? extends IUserInfo> getUserRepo() {
        return null;
    }

    /**
     * 获取用户登陆名映射表
     *
     * @return Map 机构代码映射表
     */
    @Override
    public Map<String, ? extends IUserInfo> getLoginNameRepo() {
        return null;
    }

    /**
     * 获取部门编码映射表
     *
     * @return Map 部门编码映射表
     */
    @Override
    public Map<String, UnitInfo> getDepNoRepo() {
        return null;
    }

    /**
     * 获取所有角色信息
     *
     * @return Map 所有角色信息
     */
    @Override
    public Map<String, RoleInfo> getRoleRepo() {
        return null;
    }

    /**
     * 获取业务信息
     *
     * @return Map 业务信息
     */
    @Override
    public Map<String, OptInfo> getOptInfoRepo() {
        return null;
    }

    /**
     * 获取操作方法信息
     *
     * @return Map 操作方法信息
     */
    @Override
    public Map<String, OptMethod> getOptMethodRepo() {
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @return List 所有数据字典类别信息
     */
    @Override
    public List<DataCatalog> listAllDataCatalogs() {
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @param catalogCode catalogCode
     * @return List 所有数据字典类别信息
     */
    @Override
    public List<? extends IDataDictionary> listDataDictionaries(String catalogCode) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param loginName loginName
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param userCode userCode
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regEmail regEmail
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regCellPhone regCellPhone
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        return null;
    }

    /**
     * 获取全部个人设置
     *
     * @return 个人设置列表
     */
    @Override
    public List<? extends IUserSetting> getAllSettings() {
        return null;
    }

    /**
     * 根据用户ID修改用户信息
     *
     * @param userInfo 用户信息
     */
    @Override
    public void updateUserInfo(IUserInfo userInfo) {

    }

    /**
     * 获得用户设置参数
     *
     * @param userCode  userCode
     * @param paramCode paramCode
     * @return 用户设置参数
     */
    @Override
    public IUserSetting getUserSetting(String userCode, String paramCode) {
        return null;
    }

    /**
     * 设置用户参数
     *
     * @param userSetting 用户参数， paramValue = null 则为删除
     */
    @Override
    public void saveUserSetting(IUserSetting userSetting) {

    }

    @Override
    public void insertOpt(List<? extends IOptInfo> list, List<? extends IOptMethod> list1) {

    }
}
