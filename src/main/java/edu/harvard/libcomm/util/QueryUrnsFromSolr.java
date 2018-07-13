package main.java.edu.harvard.libcomm.util;

import edu.harvard.libcomm.pipeline.Config;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CursorMarkParams;
import java.io.*;

/**
 * Created by mjv162 on 10/19/2017.
 * Based on:
 * http://www.codecognition.org/2015/03/using-cursormark-for-deep-paging-in-solr.html
 * and Solr documentation examples
 */
public class QueryUrnsFromSolr {
    private static final int MAX_ROWS=200;
    private static final String URL= Config.getInstance().DRS2_RAW_URL;
    private static final String UNIQUE_ID_FIELD="uid";
    //private static final String SOLR_QUERY="NOT targetField:[* TO *]";
    private static String type = null;
    private static String field = null;
    private static String filepath = null;

    public static void main (String[] args) {

        if (args.length != 2) {
            System.out.println("java QueryUrnsFromSolr <object|file> <filepath>");
        }
        type = args[0];
        filepath = args[1];
        if (type.equals("object")) {
            field = "object_urn_string_sort";
        }
        else if (type.equals("file")) {
            field = "file_huldrsadmin_uri_string";
        }

        try {
            QueryUrnsFromSolr qry = new QueryUrnsFromSolr();
            qry.queryForUrns();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void queryForUrns() throws Exception {
        // HttpSolrClient server = new HttpSolrClient.Builder(URL).build();
        HttpSolrClient server = new HttpSolrClient(URL);
        String currentStatus = type + "_huldrsadmin_status_string:current";
        String queryStr = "doc_type_string:" + type + " AND " + currentStatus + " AND " + field + ":[* TO *]";
        if (type.equals("object"))
            queryStr += " AND object_huldrsadmin_uriType_string:PDS";

        System.out.println(queryStr);
        SolrQuery q = (new SolrQuery(queryStr)).setFields(field).setRows(MAX_ROWS).setSort(SolrQuery.SortClause.asc("solr_id"));
        String cursorMark = CursorMarkParams.CURSOR_MARK_START;
        boolean done = false;
        FileWriter file = new FileWriter(filepath + "/" + type + "urns.txt");
        String newLine = System.getProperty("line.separator");
        while (! done) {
            q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
            QueryResponse rsp = server.query(q);
            String nextCursorMark = rsp.getNextCursorMark();
            String urns = writeUrns(rsp);
            //System.out.println(urns);
            file.write(urns + newLine);
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            }
            cursorMark = nextCursorMark;
        }
        file.close();
        System.out.println("DONE");
    }

    private String writeUrns(QueryResponse rsp) {
        SolrDocumentList docs = rsp.getResults();
        StringBuilder sb = new StringBuilder();
        for(SolrDocument doc : docs) {
            if (type.equals("file"))
                sb.append("," + doc.getFirstValue(field).toString());
            else {
                String[] urnArr = doc.getFirstValue(field).toString().split(",");
                for (String s: urnArr) {
                    if (! s.contains("DRS.OBJECT"))
                        sb.append("," + s);
                }

            }
        }
        return sb.toString().replaceFirst(",","").replace(" ","");
    }

}
