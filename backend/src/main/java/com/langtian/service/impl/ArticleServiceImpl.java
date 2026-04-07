package com.langtian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.langtian.mapper.ArticleMapper;
import com.langtian.pojo.Article;
import com.langtian.pojo.Result;
import com.langtian.service.ArticleService;
import com.langtian.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Result<String> addArticle(Article article) {
        //前端只传了text和content，没有传authorId
        // 我们从当前线程的储物柜里，把当前登录用户的 ID 拿出来！
        Map<String, Object> map = ThreadLocalUtil.get();

        // 2. 把拿到的用户 ID，强行塞进文章对象里！(这就叫后台自动补全数据)
        Integer currentUserId = (Integer) map.get("id");
        article.setAuthorId(currentUserId);

        // 摘要自动生成逻辑开始
        // 1. 如果前端没有传 summary（摘要为空）
        if (article.getSummary() == null || article.getSummary().trim().isEmpty()) {
            String content = article.getContent();
            // 2. 如果正文有内容，就截取前 50 个字符当摘要
            if (content != null && content.length() > 50) {
                article.setSummary(content.substring(0, 50) + "...");
                // 如果正文连 50 个字都不到，就直接把正文当摘要
            } else {
                article.setSummary(content);
            }
        }
        //  摘要逻辑结束

        // 3. 呼叫 MyBatis-Plus 的自带方法，一行代码存入数据库！
        // (createTime 数据库会自动生成，不需要我们管)
        articleMapper.insert(article);

        return Result.success("文章发布成功！");
    }

    @Override
    public Result<Page<Article>> list(Integer pageNum, Integer pageSize) {
        // 1. 创建分页对象 (告诉框架：我要查第 pageNum 页，每页 pageSize 条)
        Page<Article> pageInfo = new Page<>(pageNum, pageSize);

        // 2. 从储物柜拿到当前登录用户的 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer currentId = (Integer) map.get("id");

        // 3. 构造查询条件：只查 author_id 是当前用户的文章！
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("author_id", currentId);

        // 4.见证奇迹：调用 BaseMapper 自带的 selectPage 方法！
        // 它会自动去查数据，并且自动算好总条数，全都塞进 pageInfo 里！
        articleMapper.selectPage(pageInfo, wrapper);

        return Result.success(pageInfo);
    }

    @Override
    public Result<Page<Article>> publicList(Integer pageNum, Integer pageSize) {
        Page<Article> pageInfo=new Page<>(pageNum,pageSize);

        //表示不带条件查询
        QueryWrapper<Article> wrapper=new QueryWrapper<>();

        articleMapper.selectPage(pageInfo,wrapper);

        return Result.success(pageInfo);
    }

    @Override
    public Result<String> updateArticle(Article article) {
        // 1. 从储物柜拿到当前操作者的 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) map.get("id");

        // 2.核心安全防御：构造带条件的 UpdateWrapper
        UpdateWrapper<Article> wrapper = new UpdateWrapper<>();

        // 告诉数据库：要更新的文章 ID 必须是前端传来的这个
        wrapper.eq("id", article.getId());

        //  终极防御条件：并且！这篇文章的作者必须是当前登录的这个人！
        wrapper.eq("author_id", currentUserId);

        // 3. 执行更新 (注意这里用的是 update 方法，把要修改的数据和条件一起传进去)
        int count = articleMapper.update(article, wrapper);

        // 4. 判断战果：如果 count 是 0，说明没找到符合条件的数据（要么文章不存在，要么他在试图修改别人的文章！）
        if (count == 0) {
            return Result.error("更新失败：文章不存在或您无权修改此文章！");
        }

        return Result.success("文章更新成功！");
    }

    // 1. 获取文章详情 (这个不用防越权，因为博客文章通常是公开给大家看的)
    @Override
    public Result<Article> getArticleDetail(Integer id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.error("文章不存在");
        }
        return Result.success(article);
    }

    // 2.  删除文章 (极其危险的操作，必须防越权！)
    @Override
    public Result<String> deleteArticle(Integer id) {
        // 从储物柜拿到当前登录用户 ID
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) map.get("id");

        // 构造带双重条件的 Wrapper：文章ID必须对，且作者必须是当前登录的人！
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        wrapper.eq("author_id", currentUserId);

        // 执行删除
        int count = articleMapper.delete(wrapper);

        // 如果删除了 0 条，说明要么文章不存在，要么是黑客在试图删别人的文章
        if (count == 0) {
            return Result.error("删除失败：文章不存在或您无权删除！");
        }
        return Result.success("文章删除成功！");
    }
}
