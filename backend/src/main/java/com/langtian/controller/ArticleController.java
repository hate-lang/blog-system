package com.langtian.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.langtian.pojo.Article;
import com.langtian.pojo.Result;
import com.langtian.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/article/add")
    public Result<String> add(@RequestBody Article article) {
        //能够走到这里，说明拦截器已经放行
        return articleService.addArticle(article);
    }

    @GetMapping("/article/list")
    public Result<Page<Article>> list(
            @RequestParam(defaultValue = "1") Integer pageNum, // 默认第1页
            @RequestParam(defaultValue = "10") Integer pageSize // 默认每页10条
    ) {
        return articleService.list(pageNum, pageSize);
    }

    //用于公共访问的接口，无需登录携带token
    @GetMapping("/article/public/list")
    public Result<Page<Article>> publicList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        return articleService.publicList(pageNum,pageSize);
    }

    //  更新全部或大部分属性，标准规范是用 PutMapping
    @PutMapping("/article/update")
    public Result<String> updateArticle(@RequestBody Article article) {
        // 校验一下前端有没有把文章 id 传过来，如果不传 id 怎么知道更新哪篇？
        if (article.getId() == null) {
            return Result.error("文章 Id 不能为空！");
        }
        return articleService.updateArticle(article);
    }

    @GetMapping("/article/detail/{id}")
    public Result<Article> detailArticle(@PathVariable Integer id) {
        return articleService.getArticleDetail(id);
    }


   /*
   * 公共访问文章详细类容的方法，因为调用的接口中没有ThreadLocalUtil.get()拿token所以可以直接调用同一个接口。
   * 为了减小事故，直接新建一个方法
   * */
    @GetMapping("/article/public/detail/{id}")
    public Result<Article> publicDetailArticle(@PathVariable Integer id){
        return articleService.getArticleDetail(id);
    }

    @DeleteMapping("/article/delete/{id}")
    public Result<String> deleteArticle(@PathVariable Integer id) {
        return articleService.deleteArticle(id);
    }


}
