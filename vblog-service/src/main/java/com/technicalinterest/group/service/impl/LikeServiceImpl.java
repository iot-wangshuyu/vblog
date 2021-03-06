package com.technicalinterest.group.service.impl;

import com.technicalinterest.group.dao.Article;
import com.technicalinterest.group.dao.Like;
import com.technicalinterest.group.dao.Reply;
import com.technicalinterest.group.dao.WebsiteNotice;
import com.technicalinterest.group.dto.ArticlesDTO;
import com.technicalinterest.group.mapper.ArticleMapper;
import com.technicalinterest.group.mapper.LikeMapper;
import com.technicalinterest.group.mapper.ReplyMapper;
import com.technicalinterest.group.mapper.WebsiteNoticeMapper;
import com.technicalinterest.group.service.Enum.TypeEnum;
import com.technicalinterest.group.service.LikeService;
import com.technicalinterest.group.service.UserService;
import com.technicalinterest.group.service.constant.LikeConstant;
import com.technicalinterest.group.service.Enum.ResultEnum;
import com.technicalinterest.group.service.dto.LikeDTO;
import com.technicalinterest.group.service.dto.ReturnClass;
import com.technicalinterest.group.service.exception.VLogException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class LikeServiceImpl implements LikeService {

	@Autowired
	private LikeMapper likeMapper;
	@Autowired
	private ArticleMapper articleMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private WebsiteNoticeMapper websiteNoticeMapper;
	@Autowired
	private ReplyMapper replyMapper;

	/**
	 * @Description: 添加点赞
	 * @author: shuyu.wang
	 * @date: 2019/8/22 22:36
	 * @param pojo
	 * @return null
	 */
	@Override
	public ReturnClass insert(LikeDTO pojo) {
		String userName = userService.getUserNameByLoginToken();
		WebsiteNotice websiteNotice=null;
		Reply reply=null;
		//博客
		if (pojo.getType()== TypeEnum.BLOG.getCode()) {
			ArticlesDTO articleInfo = articleMapper.getArticleInfo(pojo.getSourceId(), null);
			if (Objects.isNull(articleInfo)) {
				throw new VLogException(ResultEnum.NO_URL);
			}
		//通知
		} else if (pojo.getType() == TypeEnum.NOTICE.getCode()) {
			  websiteNotice = websiteNoticeMapper.websiteNoticeById(pojo.getSourceId());
			if (Objects.isNull(websiteNotice)) {
				throw new VLogException(ResultEnum.NO_URL);
			}
			//问答
		}else if (pojo.getType() == TypeEnum.ASK.getCode()) {
			reply = replyMapper.getOneReply(pojo.getSourceId());
			if (Objects.isNull(reply)) {
				throw new VLogException(ResultEnum.NO_URL);
			}
		}else {
			return ReturnClass.fail("类型不匹配");
		}
		Like like = Like.builder().userName(userName).type(pojo.getType()).sourceId(pojo.getSourceId()).build();
		Like likeResult = likeMapper.queryLike(like);
		if (Objects.nonNull(likeResult)) {
			return ReturnClass.fail(LikeConstant.ADD_REPEAT);
		}
		like.setIpAddress(pojo.getIpAddress());
		like.setIsView((short)0);
		int insert = likeMapper.insert(like);
		if (insert > 0) {
			if (pojo.getType()==TypeEnum.BLOG.getCode()) {
				Article article = new Article();
				article.setId(pojo.getSourceId());
				article.setLikeCount(1);
				articleMapper.update(article);
			}else if (pojo.getType() == TypeEnum.NOTICE.getCode()){
				websiteNotice.setLikeCount(1);
				websiteNoticeMapper.update(websiteNotice);
			}else if (pojo.getType() == TypeEnum.ASK.getCode()){
				reply.setLikeCount(1);
				replyMapper.update(reply);
			}
			return ReturnClass.success(LikeConstant.SUS_ADD);
		}
		return ReturnClass.fail(LikeConstant.FAIL_ADD);
	}

	/**
	 * @Description: 取消点赞
	 * @author: shuyu.wang
	 * @date: 2019/8/22 22:36
	 * @param pojo
	 * @return null
	 */
	@Override
	@Transactional
	public ReturnClass del(LikeDTO pojo) {
		String userName = userService.getUserNameByLoginToken();
		Like like = Like.builder().userName(userName).type(pojo.getType()).sourceId(pojo.getSourceId()).build();
		Like likeResult = likeMapper.queryLike(like);
		if (Objects.isNull(likeResult)) {
			throw new VLogException(ResultEnum.NO_URL);
		}
		if (!StringUtils.equals(userName,likeResult.getUserName())){
			throw new VLogException(ResultEnum.NO_AUTH);
		}
		Integer integer = likeMapper.del(likeResult.getId());
		if (integer > 0) {
			if (likeResult.getType()==TypeEnum.BLOG.getCode()){
				Article article=new Article();
				article.setId(pojo.getSourceId());
				article.setLikeCount(0);
				articleMapper.update(article);
			}else if(likeResult.getType()==TypeEnum.NOTICE.getCode()){
				WebsiteNotice websiteNotice=new WebsiteNotice();
				websiteNotice.setId(pojo.getSourceId());
				websiteNotice.setLikeCount(0);
				websiteNoticeMapper.update(websiteNotice);
			} else if(likeResult.getType()==TypeEnum.NOTICE.getCode()){
				Reply reply=new Reply();
				reply.setId(pojo.getSourceId());
				reply.setLikeCount(0);
				replyMapper.update(reply);
			}else {
				throw new VLogException("类型不匹配");
			}

			return ReturnClass.success(LikeConstant.SUS_DEL);
		}
		return ReturnClass.success(LikeConstant.FAIL_DEL);
	}
}
