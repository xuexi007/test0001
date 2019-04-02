package com.offcn;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SolrClient001 {

    public static void main(String[] args) {
        String url="http://192.168.61.133:8983/solr/core1";

       // add(url);
       // update(url);
       // dele(url);
        search(url);
    }

    /**
     * 新增方法
     */
    public static void add(String url){
       //1、创建solrClient

        HttpSolrClient solrClient = new HttpSolrClient.Builder(url).build();

        //2、创建SolrInputDocument

        SolrInputDocument inputDocument = new SolrInputDocument();
        //3、设置文档对象具体字段和值
        inputDocument.setField("id","6666");
        inputDocument.setField("p_name","JAVA编程思想003");
    //  inputDocument.setField("p_cname","IT图书");
        inputDocument.setField("p_price","88.58");
       // inputDocument.setField("p_pic","1.jpg");

        //4、调用solrclient发出保存请求
        try {
            solrClient.add(inputDocument);

            //默认情况下solr是开启事务的，需要提交事务
            solrClient.commit();

            //关闭solrclient

            solrClient.close();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 修改
     */
    public static  void update(String url){
        HttpSolrClient solrClient = new HttpSolrClient.Builder(url).build();

        SolrInputDocument inputDocument = new SolrInputDocument();
        inputDocument.setField("id","11111");
        inputDocument.setField("p_name","JAVA编程思想-更新");
        inputDocument.setField("p_cname","保利文学");
        inputDocument.setField("p_price","1000");

        try {
            //根据文档id的值来判断是否更新，如果id存在就做更新，如果id不存在就做新增
            solrClient.add(inputDocument);
            solrClient.commit();

            solrClient.close();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除
     */
    public static void dele(String url){

        HttpSolrClient solrClient = new HttpSolrClient.Builder(url).build();

        try {
            //根据具体id删除
           // solrClient.deleteById("8888");

            //批量删除
           // solrClient.deleteById(Arrays.asList("9999","6666"));

            //安装查询条件删除
            solrClient.deleteByQuery("p_name:JAVA");
            solrClient.commit();

            solrClient.close();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询
     */
    public static void search(String url){

        HttpSolrClient solrClient = new HttpSolrClient.Builder(url).build();

        //1、创建查询器对象

        SolrQuery solrQuery = new SolrQuery();
        //2、设置查询关键字
        solrQuery.setQuery("p_name:包");
        //3、设置过滤条件
        solrQuery.addFilterQuery("p_price:[10 TO *]");
        solrQuery.addFilterQuery("p_cname:魅力女人");
        //4、设置排序
        solrQuery.setSort("p_price", SolrQuery.ORDER.asc);

        //5、设置游标开始位置
        solrQuery.setStart(0);
        //6、设置最大返回行数
        solrQuery.setRows(10);
        //7、设置返回字段
        solrQuery.setFields("id","p_name","p_price","p_pic","p_cname");
        //8、设置默认搜索字段
        solrQuery.set("df","p_name");

        //开启高亮
        solrQuery.setHighlight(true);
        //设置高亮字段
        solrQuery.addHighlightField("p_name");
        //设置高亮前缀
        solrQuery.setHighlightSimplePre("<em style='color:red'>");
        //设置高亮后缀
        solrQuery.setHighlightSimplePost("</em>");

        //9、发出查询请求

        try {
            QueryResponse response = solrClient.query(solrQuery);

            //从相应对象获取查询结果
            SolrDocumentList results = response.getResults();
            //获取高亮数据集合 最外层map的key--》id  第二层map的key---》p_name  list[0]
            Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
            //获取查询结果总数
            long numFound = results.getNumFound();
            System.out.println("查询结果总记录数:"+numFound);


            //遍历查询结果记录集合
            for (SolrDocument doc:results){
              //  System.out.println(doc);
                String id=(String) doc.getFieldValue("id");
                String name=(String)doc.getFieldValue("p_name");
                Float price =(Float)doc.getFieldValue("p_price");
                String pic=(String)doc.getFieldValue("p_pic");
                String catlog_name=(String)doc.getFieldValue("p_cname");
                //根据商品编号读取高亮集合
                List<String> list = highlighting.get(id).get("p_name");
                //判断高亮数据是否存在
                if(list!=null&&list.size()>0){
                   name= list.get(0);
                }

                System.out.println("商品名称:"+name+" 类目:"+catlog_name+" 价格:"+price+" 配图:"+pic);
            }

            solrClient.close();
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void demoadd(){
        System.out.println("demo1分支新增方法");
    }
}
