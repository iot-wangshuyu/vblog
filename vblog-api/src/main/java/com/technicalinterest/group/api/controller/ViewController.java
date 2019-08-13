package com.technicalinterest.group.api.controller;

import com.technicalinterest.group.api.param.NewUserParam;
import com.technicalinterest.group.api.param.QueryArticleParam;
import com.technicalinterest.group.api.param.UserParam;
import com.technicalinterest.group.api.vo.*;
import com.technicalinterest.group.dto.ArticlesDTO;
import com.technicalinterest.group.dto.QueryArticleDTO;
import com.technicalinterest.group.service.ArticleService;
import com.technicalinterest.group.service.UserService;
import com.technicalinterest.group.service.constant.ResultEnum;
import com.technicalinterest.group.service.dto.EditUserDTO;
import com.technicalinterest.group.service.dto.PageBean;
import com.technicalinterest.group.service.dto.ReturnClass;
import com.technicalinterest.group.service.exception.VLogException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @package: com.technicalinterest.group.api.controller
 * @className: ViewController
 * @description: 前端显示接口
 * @author: Shuyu.Wang
 * @date: 2019-08-12 13:07
 * @since: 0.1
 **/
@Api(tags = "前端系统接口")
@RestController
@RequestMapping("view")
public class ViewController {
	@Autowired
	private ArticleService articleService;
	@Autowired
	private UserService userService;

	private static final Boolean authCheck = false;

	/**
	 * 登录接口
	 * @return null
	 * @author: shuyu.wang
	 * @date: 2019-07-14 19:24
	 */
	@ApiOperation(value = "登录", notes = "用户登录")
	@GetMapping(value = "/user/login")
	public ApiResult<UserVO> login(@Valid UserParam userParam) {
		ApiResult apiResult = new ApiResult();
		EditUserDTO userDTO = new EditUserDTO();
		BeanUtils.copyProperties(userParam, userDTO);
		ReturnClass login = userService.login(userDTO);
		if (login.isSuccess()) {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(login.getData(), userVO);
			apiResult.success(userVO);
		} else {
			apiResult.fail(login.getMsg());
		}
		return apiResult;
	}

	/**
	 * 注册新用户
	 * @author: shuyu.wang
	 * @date: 2019-07-21 21:50
	 * @param newUserParam
	 * @return com.technicalinterest.group.api.vo.ApiResult<com.technicalinterest.group.service.vo.UserVO>
	 */
	@ApiOperation(value = "注册", notes = "新用户注册")
	@PostMapping(value = "/user/new")
	public ApiResult<String> saveUser(@Valid @RequestBody NewUserParam newUserParam) {
		ApiResult apiResult = new ApiResult();
		EditUserDTO newUserDTO = new EditUserDTO();
		BeanUtils.copyProperties(newUserParam, newUserDTO);
		ReturnClass addUser = userService.addUser(newUserDTO);
		if (addUser.isSuccess()) {
			apiResult.success(addUser.getData());
		} else {
			apiResult.fail(addUser.getMsg());
		}
		return apiResult;
	}

	/**
	 * 账号激活
	 * @author: shuyu.wang
	 * @date: 2019-07-21 22:27
	 * @return null
	 */
	@ApiOperation(value = "账号激活", notes = "激活")
	@GetMapping(value = "/user/activation/{key}")
	public ApiResult<String> activationUser(@PathVariable("key") String key) {
		ApiResult apiResult = new ApiResult();
		ReturnClass activationUser = userService.activationUser(key);
		if (activationUser.isSuccess()) {
			apiResult.success(activationUser.getMsg());
		} else {
			apiResult.fail(activationUser.getMsg());
		}
		return apiResult;
	}

	/**
	 * 用户信息接口
	 * @return null
	 * @author: shuyu.wang
	 * @date: 2019-07-14 19:24
	 */
	@ApiOperation(value = "会员用户信息", notes = "用户信息")
	@GetMapping(value = "/user/detail/{userName}")
	public ApiResult<UserVO> detail(@PathVariable("userName") String userName) {
		ApiResult apiResult = new ApiResult();
		ReturnClass getUserByuserName = userService.getUserByuserName(authCheck, userName);
		if (getUserByuserName.isSuccess()) {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(getUserByuserName.getData(), userVO);
			apiResult.success(userVO);

		} else {
			throw new VLogException(ResultEnum.NO_URL);
		}
		return apiResult;
	}

	/**
	 * @Description: 会员文章列表
	 * @author: shuyu.wang
	 * @date: 2019-08-13 12:37
	 * @param queryArticleParam
	 * @return null
	 */
	@ApiOperation(value = "会员文章列表", notes = "文章列表")
	@GetMapping(value = "/article/list/{userName}")
	public ApiResult<PageBean<ArticlesVO>> listUserArticle(@PathVariable("userName") String userName, @Valid QueryArticleParam queryArticleParam) {
		ApiResult apiResult = new ApiResult();
		QueryArticleDTO queryArticleDTO = new QueryArticleDTO();
		BeanUtils.copyProperties(queryArticleParam, queryArticleDTO);
		queryArticleDTO.setUserName(userName);
		queryArticleDTO.setState((short) 1);
		ReturnClass listArticle = articleService.listArticle(authCheck, userName, queryArticleDTO);
		if (listArticle.isSuccess()) {
			PageBean<ArticlesVO> pageInfo = new PageBean<ArticlesVO>();
			BeanUtils.copyProperties(listArticle.getData(), pageInfo);
			apiResult.success(pageInfo);

		} else {
			apiResult.setMsg(listArticle.getMsg());
		}
		return apiResult;
	}

	/**
	 * @Description: 会员最新文章
	 * @author: shuyu.wang
	 * @date: 2019-08-13 12:37
	 * @param
	 * @return null
	 */
	@ApiOperation(value = "会员最新文章列表", notes = "文章列表")
	@GetMapping(value = "/article/hot/{userName}")
	public ApiResult<ArticleTitleVO> listArticleHotByUser(@PathVariable("userName") String userName) {
		ApiResult apiResult = new ApiResult();
		ReturnClass listArticle = articleService.listArticleOrderBy(authCheck, userName, 1);
		if (listArticle.isSuccess()) {
			List<ArticleTitleVO> list = new ArrayList<ArticleTitleVO>();
			List<ArticlesDTO> articlesDTOS = (List<ArticlesDTO>) listArticle.getData();
			for (ArticlesDTO entity : articlesDTOS) {
				ArticleTitleVO articleTitleVO = new ArticleTitleVO();
				BeanUtils.copyProperties(entity, articleTitleVO);
				list.add(articleTitleVO);
			}
			apiResult.success(list);
		} else {
			apiResult.setMsg(listArticle.getMsg());
		}
		return apiResult;
	}
	//最新发布

	/**
	 * @Description: 会员热门文章
	 * @author: shuyu.wang
	 * @date: 2019-08-13 12:37
	 * @param
	 * @return null
	 */
	@ApiOperation(value = "会员热门文章列表", notes = "文章列表")
	@GetMapping(value = "/article/new/{userName}")
	public ApiResult<ArticleTitleVO> listArticleNewByUser(@PathVariable("userName") String userName) {
		ApiResult apiResult = new ApiResult();
		ReturnClass listArticle = articleService.listArticleOrderBy(authCheck, userName, 2);
		if (listArticle.isSuccess()) {
			List<ArticleTitleVO> list = new ArrayList<ArticleTitleVO>();
			List<ArticlesDTO> articlesDTOS = (List<ArticlesDTO>) listArticle.getData();
			for (ArticlesDTO entity : articlesDTOS) {
				ArticleTitleVO articleTitleVO = new ArticleTitleVO();
				BeanUtils.copyProperties(entity, articleTitleVO);
				list.add(articleTitleVO);
			}
			apiResult.success(list);
		} else {
			apiResult.setMsg(listArticle.getMsg());
		}
		return apiResult;
	}

	/**
	 * @Description: 文章详情
	 * @author: shuyu.wang
	 * @date: 2019-08-13 12:37
	 * @param id
	 * @return null
	 */
	@ApiOperation(value = "文章详情", notes = "文章详情")
	@GetMapping(value = "/article/detail/{id}")
	public ApiResult<ArticleContentVO> articleDetail(@PathVariable("id") Long id) {
		ApiResult apiResult = new ApiResult();
		ReturnClass articleDetail = articleService.articleDetail(authCheck, id);
		ArticleContentVO articleContentVO = new ArticleContentVO();
		if (articleDetail.isSuccess()) {
			PageBean<ArticlesVO> pageInfo = new PageBean<ArticlesVO>();
			BeanUtils.copyProperties(articleDetail.getData(), articleContentVO);
			apiResult.success(articleContentVO);

		} else {
			apiResult.setMsg(articleDetail.getMsg());
		}
		return apiResult;
	}

	/**
	 * 	 * @Description: 网站文章列表
	 * @author: shuyu.wang
	 * @date: 2019-08-13 12:37
	 * @param queryArticleParam
	 * @return null
	 */
	@ApiOperation(value = "整站文章列表", notes = "文章列表")
	@GetMapping(value = "/article/list")
	public ApiResult<PageBean<ArticlesVO>> listArticle(@Valid QueryArticleParam queryArticleParam) {
		ApiResult apiResult = new ApiResult();
		QueryArticleDTO queryArticleDTO = new QueryArticleDTO();
		BeanUtils.copyProperties(queryArticleParam, queryArticleDTO);
		ReturnClass listArticle = articleService.allListArticle(queryArticleDTO);
		if (listArticle.isSuccess()) {
			PageBean<ArticlesVO> pageInfo = new PageBean<ArticlesVO>();
			BeanUtils.copyProperties(listArticle.getData(), pageInfo);
			apiResult.success(pageInfo);

		} else {
			apiResult.setMsg(listArticle.getMsg());
		}
		return apiResult;
	}

	/**
	 * @Description: 网站最新文章
	 * @author: shuyu.wang
	 * @date: 2019-08-13 12:37
	 * @param
	 * @return null
	 */
	@ApiOperation(value = "最新文章列表", notes = "文章列表")
	@GetMapping(value = "/article/hot")
	public ApiResult<ArticleTitleVO> listArticleHot() {
		ApiResult apiResult = new ApiResult();
		ReturnClass listArticle = articleService.listArticleOrderBy(authCheck, null, 1);
		if (listArticle.isSuccess()) {
			List<ArticleTitleVO> list = new ArrayList<ArticleTitleVO>();
			List<ArticlesDTO> articlesDTOS = (List<ArticlesDTO>) listArticle.getData();
			for (ArticlesDTO entity : articlesDTOS) {
				ArticleTitleVO articleTitleVO = new ArticleTitleVO();
				BeanUtils.copyProperties(entity, articleTitleVO);
				list.add(articleTitleVO);
			}
			apiResult.success(list);
		} else {
			apiResult.setMsg(listArticle.getMsg());
		}
		return apiResult;
	}
	//最新发布

	/**
	 * @Description: 热门文章
	 * @author: shuyu.wang
	 * @date: 2019-08-13 12:37
	 * @param
	 * @return null
	 */
	@ApiOperation(value = "网站热门文章列表", notes = "文章列表")
	@GetMapping(value = "/article/new")
	public ApiResult<ArticleTitleVO> listArticleNew() {
		ApiResult apiResult = new ApiResult();
		ReturnClass listArticle = articleService.listArticleOrderBy(authCheck, null, 2);
		if (listArticle.isSuccess()) {
			List<ArticleTitleVO> list = new ArrayList<ArticleTitleVO>();
			List<ArticlesDTO> articlesDTOS = (List<ArticlesDTO>) listArticle.getData();
			for (ArticlesDTO entity : articlesDTOS) {
				ArticleTitleVO articleTitleVO = new ArticleTitleVO();
				BeanUtils.copyProperties(entity, articleTitleVO);
				list.add(articleTitleVO);
			}
			apiResult.success(list);
		} else {
			apiResult.setMsg(listArticle.getMsg());
		}
		return apiResult;
	}

	//归档

	//站点统计

}
