package cn.smxy.application.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.smxy.application.bean.Dictionary;
import cn.smxy.application.bean.DictionaryType;
import cn.smxy.application.bean.Menu;
import cn.smxy.application.bean.Role;
import cn.smxy.application.bean.RoleMenu;
import cn.smxy.application.bean.TreeJson;
import cn.smxy.application.bean.User;
import cn.smxy.application.bean.UserRole;
import cn.smxy.application.config.Constant;
import cn.smxy.application.core.Result;
import cn.smxy.application.core.ResultGenerator;
import cn.smxy.application.generator.CodeGenerator;
import cn.smxy.application.service.DictionaryService;
import cn.smxy.application.service.DictionaryTypeService;
import cn.smxy.application.service.MenuService;
import cn.smxy.application.service.RoleMenuService;
import cn.smxy.application.service.RoleService;
import cn.smxy.application.service.UserRoleService;
import cn.smxy.application.service.UserService;
import cn.smxy.application.utils.JwtUtil;
import cn.smxy.application.utils.SortListUtil;
import io.jsonwebtoken.Claims;
import tk.mybatis.mapper.util.StringUtil;

/**
 * @author ling_cx
 * @version 1.0
 * @Description 
 * @date 2018年1月30日 上午10:57:17
 * @Copyright: 2018 www.lingcx.cn Inc. All rights reserved.
 */
@Controller
public class CommonController extends BaseController{

	@Resource
	UserService mUserService;
	@Resource
	private UserRoleService userRoleService;
	@Resource
	private RoleService roleService;
	@Resource
	private MenuService menuService;
	@Resource
	private RoleMenuService roleMenuService;
    @Resource
    private DictionaryTypeService dictionaryTypeService;
    @Resource
    private DictionaryService dictionaryService;

	@GetMapping("/")
	public String tlogin() {
		return "redirect:login";
	}

	@GetMapping("login")
	public String login() {
		return "login";
	}

	@GetMapping("common/403")
	public String errorPage403() {
		return "403";
	}

	@GetMapping("common/404")

	public String errorPage404() {
		return "404";
	}

	@GetMapping("common/405")
	public String errorPage405() {
		return "405";
	}

	@GetMapping("refuse")
	public String refuse() {
		return "refuse";
	}

	@RequiresAuthentication
	@GetMapping("admin/index")
	public String index() {
		return "admin/index";
	}

	/**
	 * 登录操作
	 * @param username
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("loginAction")
	@ResponseBody
	public Result loginAction(@RequestParam String username, @RequestParam String password){
		logger.info("登陆操作");
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Subject currentUser = SecurityUtils.getSubject();
		try {
			logger.info("对用户[" + username + "]进行登录验证..验证开始");
			currentUser.login(token);
			logger.info("对用户[" + username + "]进行登录验证..验证通过");
			if(currentUser.isAuthenticated()) {
				logger.info("用户[" + username + "]登录认证通过(这里可以进行一些认证通过后的一些系统参数初始化操作)");
				Session session = currentUser.getSession();
				User user = mUserService.selectUserWithRole(username);
				List<Role> roles = new ArrayList<>();
				//查询用户角色
				List<UserRole> roleList = userRoleService.selectUserRoleByUserId(user.getUsId());
				for(UserRole urole:roleList) {
					Role role = urole.getUrRole();
					roles.add(role);
				}
				user.setRoles((List<Role>) SortListUtil.sort(roles,"rlOrder","asc"));
				//查询角色菜单
				List<Menu> menuList = new ArrayList<>();
				for(Role role : roles) {
					List<RoleMenu> rolemenu = roleMenuService.selectMenuByRole(role.getRlId());
					for(RoleMenu rm :rolemenu) {
						Menu menu = rm.getMenu();
						if(!menuList.contains(menu)){
							menuList.add(menu);
						}
					}
				}
				session.setAttribute(Constant.LOGIN_USER,user);
				session.setAttribute(Constant.LOGIN_USER_MENU,menuList);
				return ResultGenerator.genSuccessResult().setMessage("登录成功");
			}else {
				token.clear();
				return ResultGenerator.genSuccessResult().setMessage("登录失败");
			}
		}catch(UnknownAccountException uae){
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,未知账户");
			return ResultGenerator.genFailResult("未知账户");
		}catch(IncorrectCredentialsException ice){
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误的凭证");
			return ResultGenerator.genFailResult("密码不正确");
		}catch(LockedAccountException lae){
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,账户已锁定");
			return ResultGenerator.genFailResult("账户已锁定");
		}catch(ExcessiveAttemptsException eae){
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,错误次数大于5次,账户已锁定");
			return ResultGenerator.genFailResult("用户名或密码错误次数大于5次,账户已锁定");
		}catch (DisabledAccountException sae){
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,帐号已经禁止登录");
			return ResultGenerator.genFailResult("帐号已被禁用，请联系管理员。");
		}catch(AuthenticationException ae){
			logger.info("对用户[" + username + "]进行登录验证..验证未通过,堆栈轨迹如下");
			ae.printStackTrace();
			return ResultGenerator.genFailResult("用户名或密码不正确");
		}  
	}

	/**
	 * 用户登录后---主页面获取菜单一级菜单
	 * @param id
	 * @return
	 */
	@GetMapping("admin/GetMenuParent")
	@RequiresAuthentication
	@ResponseBody
	public Result GetMenuParent(@RequestParam int id) {
		List<Menu> resultMenuList = new ArrayList<>();
		List<Menu> menuList = GetMenuSesseion();
		for(Menu menu :menuList) {
			if(menu.getMuPid() == id) {
				if(!resultMenuList.contains(menu)){
					resultMenuList.add(menu);
				}
			}
		}
		@SuppressWarnings("unchecked")
		List<Menu> menuListOrder = (List<Menu>)SortListUtil.sort(resultMenuList,"muOrder",SortListUtil.ASC);
		return ResultGenerator.genSuccessResult(menuListOrder);
	}

	/**
	 * 用户登录后---根据id获取子菜单
	 * @param id
	 * @return
	 */
	@GetMapping("admin/GetMenuChildren")
	@ResponseBody
	public Result GetMenuChildren(@RequestParam int id) {
		List<TreeJson> tjs=new ArrayList<TreeJson>();  
		List<Menu> resultMenuList = new ArrayList<>();
		List<Menu> menuList = GetMenuSesseion();
		for(Menu menu :menuList) {
			if(menu.getMuPid() == id) {
				if(!resultMenuList.contains(menu)){
					resultMenuList.add(menu);
				}
			}
		}
		@SuppressWarnings("unchecked")
		List<Menu> menuListOrder = (List<Menu>)SortListUtil.sort(resultMenuList,"muOrder",SortListUtil.ASC);
		MenuToTreeJson(tjs, menuListOrder);  
		TreeJson root = new TreeJson(); 
		List<TreeJson> treelist = new ArrayList<TreeJson>();//拼凑好的json格式的数据       
		if (tjs != null && tjs.size() > 0) {  
			for(int i= 0; i < tjs.size(); i++){  
				//如果该节点没有父节点那么它就是根（root）节点  
				if(tjs.get(i).getPid() == id){  
					root = tjs.get(i) ;  
					//获取该根节点的子节点  
					TreeJson.getChildrenNodes(tjs,root);  
					treelist.add(root) ;  
				}  
			}  
		}        
		return ResultGenerator.genSuccessResult(treelist);
	}
	
	@RequiresAuthentication
	@PostMapping("admin/selectType/{code}")
	@ResponseBody
	public Result selectType(@PathVariable String code) {
		DictionaryType dt = dictionaryTypeService.selectByCode(code);
		Map<String,Object> params1 = new HashMap<String, Object>();
		params1.put("stype", 2);
		params1.put("skey", dt.getDtId());
		List<Dictionary> list = dictionaryService.selectDictionaryByCondition(params1);
		return ResultGenerator.genSuccessResult(list);
	}
	
	@RequiresAuthentication
	@PostMapping("admin/selectTypes/{code}")
	@ResponseBody
	public Result selectTypes(@PathVariable String code) {
		List<Dictionary> resultList = new ArrayList<>();
		String cd[] = code.split("&");
		for(int i = 0 ;i < cd.length; i++) {
			DictionaryType dt = dictionaryTypeService.selectByCode(cd[i]);
			Map<String,Object> params1 = new HashMap<String, Object>();
			params1.put("stype", 2);
			params1.put("skey", dt.getDtId());
			List<Dictionary> list = dictionaryService.selectDictionaryByCondition(params1);
			resultList.addAll(list);
		}
		return ResultGenerator.genSuccessResult(resultList);
	}


	/**
	 * 注销登录，通过配置文件交给shiro处理
	 * @return
	 */
	@GetMapping("logout")
	@ResponseBody
	public Result logout() {
		return ResultGenerator.genSuccessResult("退出成功");
	}

	/*****************************************JSON WEN TOKEN TEST*******************************************/
	@GetMapping("JWTTest")
	public Result JWTTest() {
		Subject currentUser = SecurityUtils.getSubject();
		Session session = currentUser.getSession();
		User user = (User)session.getAttribute(Constant.LOGIN_USER);
		try {
			String token = JwtUtil.createJWT(Constant.JWT_ID, JwtUtil.generalSubject(user), Constant.JWT_TTL);
			System.out.println("Token:"+token);
			return ResultGenerator.genFailResult(token);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@GetMapping("JWTParse")
	public Result JWTParse(String token) {
		Claims claims;
		try {
			claims = JwtUtil.parseJWT(token);
			System.out.println("Subject:"+claims.getSubject());
			System.out.println("Id:"+claims.getId());
			System.out.println("Expiration:"+claims.getExpiration());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ResultGenerator.genFailResult("123");
	}

	@PostMapping("GenCode")
	public Result GenCode(@RequestParam String tablename,@RequestParam String classname) {
		if(StringUtil.isEmpty(classname)) {
			CodeGenerator.genCode(tablename);
		}else {
			CodeGenerator.genCode(tablename,classname);
		}
		return ResultGenerator.genSuccessResult().setMessage("生成成功");
	}
}
