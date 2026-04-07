package com.langtian.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.langtian.pojo.Article;
import com.langtian.pojo.Result;

public interface ArticleService {

     //添加文章
     Result<String> addArticle(Article article);

     //分页查询文章
     Result<Page<Article>> list(Integer pageNum,Integer pageSize);

     //公共分页查询文章
     Result<Page<Article>> publicList(Integer pageNum, Integer pageSize);

     //更新文章
     Result<String> updateArticle(Article article);

     //获取文章详细类容
     Result<Article> getArticleDetail(Integer id);

     //删除文章
     Result<String> deleteArticle(Integer id);


}
