package com.centit.framework.cloud;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.model.basedata.*;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.security.model.JsonCentitUserDetails;
import com.centit.framework.staticsystem.po.*;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CloudPlatformEnvironment implements PlatformEnvironment {

    @Autowired
    RestTemplate restTemplate;

    protected String FRAMEWORK_SERVER_URL;

    @Value("${serives.framework.url:http://FRAMEWORK-SERVICE}")
    public void setFrameworkUrl(String frameworkUrl){
        FRAMEWORK_SERVER_URL = frameworkUrl +"/platform";
    }

    private Logger logger = LoggerFactory.getLogger(CloudPlatformEnvironment.class);

    private String topOptId;

    public void setTopOptId(String topOptId) {
        this.topOptId = topOptId;
    }

    public CloudPlatformEnvironment() {
        this.topOptId="DUMMY";
    }

    @Override
    @HystrixCommand(fallbackMethod = "dummyListAllRolePower")
    public List<RolePower>  listAllRolePower(){
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/allrolepowers/"+topOptId,
                        String.class));
        return receiveJSON.getDataAsArray(RolePower.class);
    }

    public List<RolePower>  dummyListAllRolePower(){
        return null;
    }

    @Override
    @HystrixCommand(fallbackMethod = "dummyListAllOptMethod")
    public List<OptMethod> listAllOptMethod(){
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/alloptmethods/"+topOptId,
                        String.class));
        return receiveJSON.getDataAsArray(OptMethod.class);
    }

    public List<OptMethod>  dummyListAllOptMethod(){
        return null;
    }
    /**
     * @return 所有的数据范围定义表达式
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListAllOptDataScope")
    public List<OptDataScope> listAllOptDataScope() {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
            restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/alloptdatascopes/"+topOptId,
                String.class));
        return receiveJSON.getDataAsArray(OptDataScope.class);
    }

    public List<OptDataScope>  dummyListAllOptDataScope(){
        return null;
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
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/usermenu/"+superOptId+"/"+userCode+"?asAdmin="+asAdmin,
                        String.class));
        return receiveJSON.getDataAsArray(OptInfo.class);

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
    @HystrixCommand(fallbackMethod = "dummyListUserRoles")
    public List<UserRole> listUserRoles(String userCode){
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/userroles/"+userCode,
                        String.class));
        return receiveJSON.getDataAsArray(UserRole.class);
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
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/roleusers/"+roleCode,
                        String.class));
        return receiveJSON.getDataAsArray(UserRole.class);
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
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/unitroles/"+unitCode,
                        String.class));
        return receiveJSON.getDataAsArray(UnitRole.class);
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
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(FRAMEWORK_SERVER_URL+"/roleunits/"+roleCode,
                        String.class));
        return receiveJSON.getDataAsArray(UnitRole.class);
    }

    public List<UnitRole> dummyListRoleUnits(String roleCode) {
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
    @HystrixCommand(fallbackMethod = "dummyListAllUsers")
    public List<UserInfo> listAllUsers() {
        String jsonString =
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/allusers/"+topOptId,
                        String.class);
        HttpReceiveJSON httpReceiveJSON = HttpReceiveJSON.valueOfJson(jsonString);

        return httpReceiveJSON.getDataAsArray(UserInfo.class);

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
    @HystrixCommand(fallbackMethod = "dummyListAllUnits")
    public List<UnitInfo> listAllUnits() {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/allunits/"+topOptId,
                        String.class));
        return receiveJSON.getDataAsArray(UnitInfo.class);
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
    @HystrixCommand(fallbackMethod = "dummyListAllUserUnits")
    public List<UserUnit> listAllUserUnits() {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/alluserunits/"+topOptId,
                        String.class));
        return receiveJSON.getDataAsArray(UserUnit.class);
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
    @HystrixCommand(fallbackMethod = "dummyListUserUnits")
    public List<UserUnit> listUserUnits(String userCode) {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/userunits/"+topOptId+"/"+userCode,
                        String.class));
        return receiveJSON.getDataAsArray(UserUnit.class);
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
    @HystrixCommand(fallbackMethod = "dummyListUnitUsers")
    public List<UserUnit> listUnitUsers(String unitCode) {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/unitusers/"+topOptId+"/"+unitCode,
                        String.class));
        return receiveJSON.getDataAsArray(UserUnit.class);
    }

    public List<UserUnit> dummyListUnitUsers(String unitCode) {
        return null;
    }


    /**
     * 获取所有数据字典类别信息
     *
     * @return List 所有数据字典类别信息
     */
    @Override
    @HystrixCommand(fallbackMethod = "dummyListAllDataCatalogs")
    public List<DataCatalog> listAllDataCatalogs() {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/catalogs/"+topOptId,
                        String.class));
        return receiveJSON.getDataAsArray(DataCatalog.class);

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
    @HystrixCommand(fallbackMethod = "dummyListDataDictionaries")
    public List<DataDictionary> listDataDictionaries(String catalogCode) {
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/dictionary/"+topOptId+"/"+catalogCode,
                        String.class));
        return receiveJSON.getDataAsArray(DataDictionary.class);
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
        HttpReceiveJSON resJson = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/userdetails/"+topOptId+"/"+queryParam+"?qtype="+qtype,
                        String.class));

        if(resJson==null || resJson.getCode()!=0) {
            return null;
        }
        JsonCentitUserDetails userDetails =
                resJson.getDataAsObject("userDetails", JsonCentitUserDetails.class);
        userDetails.getUserInfo().put("userPin", resJson.getDataAsString("userPin"));
        userDetails.setUserUnits(
                (JSONArray) resJson.getData("userUnits"));//, UserUnit.class));
        userDetails.setAuthoritiesByRoles(userDetails.getUserRoles());
        return userDetails;
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
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson(
                restTemplate.getForObject(
                        FRAMEWORK_SERVER_URL+"/usersetting/"+userCode+"/"+paramCode,
                        String.class));
        if (null == receiveJSON) {
            return null;
        }
        return receiveJSON.getDataAsObject(UserSetting.class);
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

    /**
     * 新增菜单和操作
     *
     * @param optInfos   菜单对象集合
     * @param optMethods 操作对象集合
     */
    @Override
    public void insertOrUpdateMenu(List<? extends IOptInfo> optInfos, List<? extends IOptMethod> optMethods) {

    }

    @Override
    public List<? extends IRoleInfo> listAllRoleInfo() {
        return null;
    }

    @Override
    public List<? extends IOptInfo> listAllOptInfo() {
        return null;
    }

    @Override
    public List<? extends IUserSetting> listUserSettings(String userCode) {
        return null;
    }
}
