package com.briup.cms.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.briup.cms.bean.Article;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ReadNumTask {

    /**
     * 将阅读量刷新到MySQL
     */
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ArticleDao articleDao;
    //redis当中阅读量的key
    private final String READ_NUM = "Article_Read_Num";

    // 5分钟运行一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void saveReadNum(){
        //获取
        try{
            //遍历redis当中拿到的map集合
            Map<Object, Object> article_read_num = redisUtil.getHash(READ_NUM);
            Set<Map.Entry<Object, Object>> entries = article_read_num.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                //获取key（文章id）
                Long articleId = Long.valueOf(entry.getKey().toString());
                //获取value（阅读量）
                Integer readNum = Integer.valueOf(entry.getValue().toString());
                Article article = new Article();
                article.setId(articleId);
                article.setReadNum(readNum);
                //更新
                articleDao.updateById(article);
            }
            log.info("阅读量更新入库完毕");
        }catch (Exception e){
            log.info("阅读量入库失败，原因为：" + e.getMessage());
        }

    }
}
