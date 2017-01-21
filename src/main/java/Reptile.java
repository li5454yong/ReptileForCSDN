import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/1/20.
 */
public class Reptile {


    static Pattern p = null;
    static Matcher m = null;

    public static void main(String[] args) throws IOException {
        String url = "http://blog.csdn.net/u283056051/article/details/52187091";
        Document doc = Jsoup.connect(url)
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Referer", "https://www.baidu.com/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                .timeout(5000)
                .get();

        Element div = doc.getElementById("article_details");

        Elements span = div.select("span.link_title");
        Element a = span.get(0).child(0);//获取文章名称
        String title = a.html();
        System.out.println(a.html());

        Elements postDate = div.select("span.link_postdate");
        String date = postDate.get(0).html();
        date = Pattern.compile("-").matcher(date).replaceAll("/");
        System.out.println(date);


        Element atricle_content = div.getElementById("article_content");


        String html = atricle_content.html();
        //System.out.println(html);

        //去除js代码
        String regEx = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        p = Pattern.compile(regEx);
        m = p.matcher(html);
        String okContent = null;
        if (m.find()) {
            okContent = m.replaceAll("");
        }

        //去除div标签
        p = Pattern.compile("<div (.*?)>");
        m = p.matcher(okContent);
        if(m.find()){
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("</div>");
        m = p.matcher(okContent);

        if(m.find()){
            okContent = m.replaceAll("");
        }

        //去除span标签
        p = Pattern.compile("<span (.*?)>");
        m = p.matcher(okContent);
        if(m.find()){
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("</span>");
        m = p.matcher(okContent);

        if(m.find()){
            okContent = m.replaceAll("");
        }


        //去除p标签
        p = Pattern.compile("<p (.*?)>");
        m = p.matcher(okContent);
        if(m.find()){
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("</p>");
        m = p.matcher(okContent);

        if(m.find()){
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("<p>");
        m = p.matcher(okContent);

        if(m.find()){
            okContent = m.replaceAll("");
        }

        //去除span标签
        p = Pattern.compile("&lt;");
        m = p.matcher(okContent);
        if(m.find()){
            okContent = m.replaceAll("<");
        }
        p = Pattern.compile("&gt;");
        m = p.matcher(okContent);

        if(m.find()){
            okContent = m.replaceAll(">");
        }

        System.out.println(okContent);



        FileInputStream fileInputStream = new FileInputStream("E:\\123.xml");
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        String line = reader.readLine(); // 读取第一行
        StringBuffer buffer = new StringBuffer();
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        fileInputStream.close();

        String page = replace(buffer.toString(),"title",title);

        page = replace(page,"content",okContent);

        page = replace(page,"post_date",date);

        RandomAccessFile randomFile = null;
        randomFile = new RandomAccessFile("E:\\456.xml", "rw");
        randomFile.seek(randomFile.length());
        randomFile.write(page.getBytes());

        randomFile.close();

    }

    static String replace(String str,String name,String content){

        p = Pattern.compile("\\{"+name+"\\}");
        m = p.matcher(str);

        str = m.replaceAll(content);

        return str;
    }
}
