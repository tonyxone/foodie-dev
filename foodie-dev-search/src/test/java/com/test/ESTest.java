package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    /*
    * 不建议使用 ElasticsearchTemplate 对索引进行管理（创建索引，更新映射，删除索引）
    * 索引就像数据库或数据库中的表，我们平时是不会通过java代码频繁的去创建修改数据库或表
    * 只会针对数据做crud的操作
    * */

    @Test
    public void createIndexStu(){

        Stu stu = new Stu();
        stu.setStuId(1004L);
        stu.setName("shit name");
        stu.setAge(72);
        stu.setMoney(14388.8f);
        stu.setSign("i am a shit man");
        stu.setDescription("I wish I am a no man");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery);
    }

    @Test
    public void deleteIndexStu(){

        esTemplate.deleteIndex(Stu.class);

    }

    //----------------------------------------------------------------------

    @Test
    public void updateStuDoc(){

        Map<String,Object> sourceMap = new HashMap<>();
        sourceMap.put("sign","I am not super man");
        sourceMap.put("money",88.6f);
        sourceMap.put("age",33);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                                        .withClass(Stu.class)
                                        .withId("1001")
                                        .withIndexRequest(indexRequest)
                                        .build();

        //update stu set sign = 'abc',age =33,money=88.6,where dicId='1002'
        esTemplate.update(updateQuery);
    }

    @Test
    public void getIndexStu(){

        GetQuery query = new GetQuery();
        query.setId("1001");

        Stu stu = esTemplate.queryForObject(query,Stu.class);
        System.out.println(stu.toString());
    }

    @Test
    public void deleteIndexDoc(){
        esTemplate.delete(Stu.class,"1001");
    }


    @Test
    public void searchStuDoc(){

        Pageable pageable = PageRequest.of(0,2);

        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description","shit man"))
                .withPageable(pageable).build();

        AggregatedPage<Stu> pagedStu = esTemplate.queryForPage(query,Stu.class);
        pagedStu.getTotalPages();
        System.out.println("检索后的总分页数目为:" + pagedStu.getTotalPages());
        List<Stu> stuList = pagedStu.getContent();
        for(Stu s : stuList){
            System.out.println(s);
        }
    }


    @Test
    public void highlightStuDoc(){

        Pageable pageable = PageRequest.of(0,10);

        String preTag = "<font color = 'red'>";
        String postTag = "</font>";

        SortBuilder sortBuilder = new FieldSortBuilder("money").order(SortOrder.ASC);

        SearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description","shit man"))
                .withPageable(pageable)
                .withHighlightFields(new HighlightBuilder.Field("description")
                        .preTags(preTag)
                        .postTags(postTag))
                .withSort(sortBuilder)
                .build();

        AggregatedPage<Stu> pagedStu = esTemplate.queryForPage(query, Stu.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<Stu> stuListHighLight = new ArrayList<>();

                SearchHits hits = searchResponse.getHits();
                for(SearchHit h : hits){
                    HighlightField highlightField =  h.getHighlightFields().get("description");
                    String description = highlightField.getFragments()[0].toString();

                    Object stuId = (Object)h.getSourceAsMap().get("stuId");
                    String name = (String)h.getSourceAsMap().get("name");
                    Integer age = (Integer) h.getSourceAsMap().get("age");
                    String sign = (String)h.getSourceAsMap().get("sign");
                    Object money = (Object)h.getSourceAsMap().get("money");

                    Stu stuHL = new Stu();
                    stuHL.setDescription(description);
                    stuHL.setStuId(Long.valueOf(stuId.toString()));
                    stuHL.setName(name);
                    stuHL.setAge(age);
                    stuHL.setSign(sign);
                    stuHL.setMoney(Float.valueOf(money.toString()));


                    stuListHighLight.add(stuHL);
                }

                if(stuListHighLight.size()>0){
                    return new AggregatedPageImpl<>((List<T>)stuListHighLight);
                }

                return null;
            }
        });
        pagedStu.getTotalPages();
        System.out.println("检索后的总分页数目为:" + pagedStu.getTotalPages());
        List<Stu> stuList = pagedStu.getContent();
        for(Stu s : stuList){
            System.out.println(s);
        }
    }
}
