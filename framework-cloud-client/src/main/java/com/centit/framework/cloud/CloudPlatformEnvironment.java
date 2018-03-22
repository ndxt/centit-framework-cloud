package com.centit.framework.cloud;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.RestfulHttpRequest;
import com.centit.framework.common.ResponseJSON;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitSecurityMetadata;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.OptTreeNode;
import com.centit.framework.staticsystem.po.*;
import com.centit.framework.staticsystem.security.StaticCentitUserDetails;
import com.centit.support.network.HttpExecutor;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
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
import java.util.HashMap;
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
    @HystrixCommand(fallbackMethod = "dummyListUserRolesByUserCode")
    public List<RoleInfo> listUserRolesByUserCode(String userCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/userroleinfos/"+userCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(RoleInfo.class);
    }

    public List<RoleInfo> dummyListUserRolesByUserCode(String userCode) {
        return null;
    }

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListRoleUserByRoleCode")
    public List<UserInfo> listRoleUserByRoleCode(String roleCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/roleuserinfos/"+roleCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UserInfo.class);
    }

    public List<UserInfo> dummyListRoleUserByRoleCode(String roleCode) {
        return null;
    }

    /**
     * 获取用户所有角色
     *
     * @param userCode 用户代码
     * @return List 用户所有菜单功能
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListUserRoles")
    public List<UserRole> listUserRoles(String userCode){
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/userroles/"+userCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UserRole.class);
    }

    public List<UserRole> dummyListUserRoles(String userCode){
        return null;
    }

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListRoleUsers")
    public List<UserRole> listRoleUsers(String roleCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/roleusers/"+roleCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UserRole.class);
    }

    public List<UserRole> dummyListRoleUsers(String roleCode) {
        return null;
    }

    /**
     * 获取用户所有角色
     *
     * @param unitCode 机构代码
     * @return List 用户所有菜单功能
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListUnitRoles")
    public List<UnitRole> listUnitRoles(String unitCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/unitroles/"+unitCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UnitRole.class);
    }

    public List<UnitRole> dummyListUnitRoles(String unitCode) {
        return null;
    }

    /**
     * 获取拥有该角色的所有用户
     *
     * @param roleCode 角色代码
     * @return List 用户所有菜单功能
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListRoleUnits")
    public List<UnitRole> listRoleUnits(String roleCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/roleunits/"+roleCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UnitRole.class);
    }

    public List<UnitRole> dummyListRoleUnits(String roleCode) {
        return null;
    }

    /**
     * 根据用户代码获取用户信息，
     *
     * @param userCode userCode
     * @return 用户信息
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyGetUserInfoByUserCode")
    public UserInfo getUserInfoByUserCode(String userCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/userinfo/"+userCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsObject(UserInfo.class);
    }

    public UserInfo dummyGetUserInfoByUserCode(String userCode) {
        return null;
    }

    /**
     * 根据登录名获取用户信息，
     *
     * @param loginName loginName
     * @return 登录名获取用户信息
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyGetUserInfoByLoginName")
    public UserInfo getUserInfoByLoginName(String loginName) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/userinfobyloginname/"+loginName,
                        ResponseJSON.class);
        return responseJSON.getDataAsObject(UserInfo.class);

    }

    public UserInfo dummyGetUserInfoByLoginName(String loginName) {
        return null;
    }

    /**
     * 根据用户代码获取用户信息，
     *
     * @param unitCode unitCode
     * @return 用户信息
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyGetUnitInfoByUnitCode")
    public UnitInfo getUnitInfoByUnitCode(String unitCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/unitinfo/"+unitCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsObject(UnitInfo.class);

    }

    public UnitInfo dummyGetUnitInfoByUnitCode(String unitCode) {
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
    @Cacheable(value = "UserInfo",key = "'userList'" )
    @HystrixCommand(fallbackMethod = "dummyListAllUsers")
    public List<UserInfo> listAllUsers() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/allusers/"+topOptId,
                        ResponseJSON.class);
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
    @Cacheable(value="UnitInfo",key="'unitList'")
    @HystrixCommand(fallbackMethod = "dummyListAllUnits")
    public List<UnitInfo> listAllUnits() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/allunits/"+topOptId,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UnitInfo.class);
    }

    public List<UnitInfo> dummyListAllUnits() {
        return null;
    }

    /**
     * 获取所有用户和机构关联关系
     *
     * @return List 所有用户和机构关联关系
     */
    @Override
    @Cacheable(value="AllUserUnits",key="'allUserUnits'")
    @HystrixCommand(fallbackMethod = "dummyListAllUserUnits")
    public List<UserUnit> listAllUserUnits() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/alluserunits/"+topOptId,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UserUnit.class);
    }

    public List<UserUnit> dummyListAllUserUnits() {
        return null;
    }

    /**
     * 根据用户代码获得 用户所有的机构信息
     *
     * @param userCode userCode
     * @return List 用户所有的机构信息
     */
    @Override
    @Cacheable(value="UserUnits",key="#userCode")
    @HystrixCommand(fallbackMethod = "dummyListUserUnits")
    public List<UserUnit> listUserUnits(String userCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/userunits/"+topOptId+"/"+userCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UserUnit.class);
    }

    public List<UserUnit> dummyListUserUnits(String userCode) {
        return null;
    }

    /**
     * 根据机构代码获得 机构所有用户信息
     *
     * @param unitCode unitCode
     * @return List 机构所有用户信息
     */
    @Override
    @Cacheable(value="UnitUsers",key="#unitCode")
    @HystrixCommand(fallbackMethod = "dummyListUnitUsers")
    public List<UserUnit> listUnitUsers(String unitCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/unitusers/"+topOptId+"/"+unitCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UserUnit.class);
    }

    public List<UserUnit> dummyListUnitUsers(String unitCode) {
        return null;
    }

    /**
     * 获取机构代码映射表
     *
     * @return Map 机构代码映射表
     */
    @Override
    @Cacheable(value="UnitInfo",key="'unitCodeMap'")
    @HystrixCommand(fallbackMethod = "dummyGetUnitRepo")
    public Map<String, UnitInfo> getUnitRepo() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/unitrepo/"+topOptId,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsMap(UnitInfo.class);
    }

    public Map<String, UnitInfo> dummyGetUnitRepo() {
        return null;
    }

    /**
     * 获取部门编码映射表
     *
     * @return map 部门编码映射表
     */
    @Override
    @Cacheable(value = "UserInfo",key = "'userCodeMap'" )
    @HystrixCommand(fallbackMethod = "dummyGetUserRepo")
    public Map<String, UserInfo> getUserRepo() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/userrepo/"+topOptId,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsMap(UserInfo.class);
    }

    public Map<String, UserInfo> dummyGetUserRepo() {
        return null;
    }

    /**
     * 获取用户登陆名映射表
     *
     * @return Map 机构代码映射表
     */
    @Override
    @Cacheable(value = "UserInfo",key = "'loginNameMap'")
    @HystrixCommand(fallbackMethod = "dummyGetLoginNameRepo")
    public Map<String, UserInfo> getLoginNameRepo() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/loginnamerepo/"+topOptId,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsMap(UserInfo.class);
    }

    public Map<String, UserInfo> dummyGetLoginNameRepo() {
        return null;
    }

    /**
     * 获取部门编码映射表
     *
     * @return Map 部门编码映射表
     */
    @Override
    @Cacheable(value="UnitInfo",key="'depNoMap'")
    @HystrixCommand(fallbackMethod = "dummyGetDepNoRepo")
    public Map<String, UnitInfo> getDepNoRepo() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/depnorepo/"+topOptId,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsMap(UnitInfo.class);

    }

    public Map<String, UnitInfo> dummyGetDepNoRepo() {
        return null;
    }

    /**
     * 获取所有角色信息
     *
     * @return Map 所有角色信息
     */
    @Override
    @Cacheable(value="RoleInfo",key="'roleCodeMap'")
    @HystrixCommand(fallbackMethod = "dummyGetRoleRepo")
    public Map<String, RoleInfo> getRoleRepo() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/rolerepo/"+topOptId,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsMap(RoleInfo.class);
    }

    public Map<String, RoleInfo> dummyGetRoleRepo() {
        return null;
    }

    /**
     * 获取业务信息
     *
     * @return Map 业务信息
     */
    @Override
    @Cacheable(value="OptInfo",key="'optIdMap'")
    @HystrixCommand(fallbackMethod = "dummyGetOptInfoRepo")
    public Map<String, OptInfo> getOptInfoRepo() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/optinforepo/"+topOptId,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsMap(OptInfo.class);
    }

    public Map<String, OptInfo> dummyGetOptInfoRepo() {
        return null;
    }

    /**
     * 获取操作方法信息
     *
     * @return Map 操作方法信息
     */
    @Override
    @Cacheable(value="OptInfo",key="'optCodeMap'")
    @HystrixCommand(fallbackMethod = "dummyGetOptMethodRepo")
    public Map<String, OptMethod> getOptMethodRepo() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/optmethodrepo/"+topOptId,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsMap(OptMethod.class);
    }

    public Map<String, OptMethod> dummyGetOptMethodRepo() {
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @return List 所有数据字典类别信息
     */
    @Override
    @Cacheable(value = "DataDictionary",key="'CatalogCode'")
    @HystrixCommand(fallbackMethod = "dummyListAllDataCatalogs")
    public List<DataCatalog> listAllDataCatalogs() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/catalogs/"+topOptId,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(DataCatalog.class);

    }

    public List<DataCatalog> dummyListAllDataCatalogs() {
        return null;
    }

    /**
     * 获取所有数据字典类别信息
     *
     * @param catalogCode catalogCode
     * @return List 所有数据字典类别信息
     */
    @Override
    @Cacheable(value = "DataDictionary",key="#catalogCode")
    @HystrixCommand(fallbackMethod = "dummyListDataDictionaries")
    public List<DataDictionary> listDataDictionaries(String catalogCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/dictionary/"+topOptId+"/"+catalogCode,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(DataDictionary.class);
    }

    public List<DataDictionary> dummyListDataDictionaries(String catalogCode) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param loginName loginName
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyLoadUserDetailsByLoginName")
    public CentitUserDetails loadUserDetailsByLoginName(String loginName) {
        return loadUserDetails(loginName,"loginName");
    }

    public CentitUserDetails dummyLoadUserDetailsByLoginName(String loginName) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param userCode userCode
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyLoadUserDetailsByUserCode")
    public CentitUserDetails loadUserDetailsByUserCode(String userCode) {
        return loadUserDetails(userCode,"userCode");
    }

    public CentitUserDetails dummyLoadUserDetailsByUserCode(String userCode) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regEmail regEmail
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyLoadUserDetailsByRegEmail")
    public CentitUserDetails loadUserDetailsByRegEmail(String regEmail) {
        return loadUserDetails(regEmail,"regEmail");
    }

    public CentitUserDetails dummyLoadUserDetailsByRegEmail(String regEmail) {
        return null;
    }

    /**
     * 获取用户信息放到Session中，内容包括用户基本信息，用户机构信息，用户权限信息等等
     *
     * @param regCellPhone regCellPhone
     * @return 用户基本信息，用户机构信息，用户权限信息等等
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyLoadUserDetailsByRegCellPhone")
    public CentitUserDetails loadUserDetailsByRegCellPhone(String regCellPhone) {
        return loadUserDetails(regCellPhone,"regCellPhone");
    }

    public CentitUserDetails dummyLoadUserDetailsByRegCellPhone(String regCellPhone) {
        return null;
    }

    private CentitUserDetails loadUserDetails(String queryParam, String qtype) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/userdetails/"+topOptId+"/"+queryParam+"?qtype="+qtype,
                        ResponseJSON.class);

        if(responseJSON==null || responseJSON.getCode()!=0) {
            return null;
        }
        StaticCentitUserDetails userDetails =
                responseJSON.getDataAsObject("userDetails", StaticCentitUserDetails.class);
        userDetails.getUserInfo().setUserUnits(
                responseJSON.getDataAsArray("userUnits", UserUnit.class) );
        userDetails.setAuthoritiesByRoles(userDetails.getUserRoles());
        return userDetails;
    }

    /**
     * 获取全部个人设置
     *
     * @return 个人设置列表
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyGetAllSettings")
    public List<UserSetting> getAllSettings() {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/allsettings/"+topOptId,
                        ResponseJSON.class);
        return responseJSON.getDataAsArray(UserSetting.class);
    }

    public List<UserSetting> dummyGetAllSettings() {
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
    @HystrixCommand(fallbackMethod = "dummyGetUserSetting")
    public UserSetting getUserSetting(String userCode, String paramCode) {
        ResponseJSON responseJSON =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/usersetting/"+userCode+"/"+paramCode,
                        ResponseJSON.class);
        if (null == responseJSON) {
            return null;
        }
        return responseJSON.getDataAsObject(UserSetting.class);
    }

    public UserSetting dummyGetUserSetting(String userCode, String paramCode) {
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
