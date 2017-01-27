import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CSDN博客备份工具
 * Created by lxg on 2017/1/20.
 */
public class Reptile {


    static Pattern p = null;
    static Matcher m = null;

    /**
     * 获取文章内容节点
     *
     * @param articleUrl
     * @return
     * @throws IOException
     */
    static Element getContentElement(String articleUrl) throws IOException {
        String url = "http://blog.csdn.net" + articleUrl;
        Document doc = Jsoup.connect(url)
                .header("Accept", "*/*")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Referer", "https://www.baidu.com/")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                .timeout(5000)
                .get();
        Element div = doc.getElementById("article_details");

        return div;
    }

    /**
     * 获取文章标题
     *
     * @param div
     * @return
     */
    static String getTitle(Element div) {
        Elements span = div.select("span.link_title");
        Element a = span.get(0).child(0);//获取文章名称
        String title = a.html();
        System.out.println(a.html());

        return title;
    }

    /**
     * 判断文章是原创还是转载
     * @param div
     * @return
     */
    static boolean getArticlType(Element div){
        Elements span = div.select("span.ico_type_Repost");
        if(span.size() == 0){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 获取文章发布时间
     *
     * @param div
     * @return
     */
    static String getPostDate(Element div) {
        Elements postDate = div.select("span.link_postdate");
        String date = postDate.get(0).html();

        return date;
    }

    /**
     * 获取文章正文
     *
     * @param div
     * @return
     */
    static String getAtricleContent(Element div) {
        Element atricle_content = div.getElementById("article_content");
        String atricle = atricle_content.html();
        return atricle;
    }

    /**
     * 去除冗余的html标签
     * @param atricle
     * @return
     */
    static String replaceRedundancyElement(String atricle) {

        String okContent = atricle;
        //去除js代码
        String regEx = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        p = Pattern.compile(regEx);
        m = p.matcher(okContent);
        if (m.find()) {
            okContent = m.replaceAll("");
        }

        //去除div标签
        p = Pattern.compile("<div (.*?)>");
        m = p.matcher(okContent);
        if (m.find()) {
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("</div>");
        m = p.matcher(okContent);

        if (m.find()) {
            okContent = m.replaceAll("");
        }

        //去除span标签
        p = Pattern.compile("<span (.*?)>");
        m = p.matcher(okContent);
        if (m.find()) {
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("</span>");
        m = p.matcher(okContent);

        if (m.find()) {
            okContent = m.replaceAll("");
        }


        //去除p标签
        p = Pattern.compile("<p (.*?)>");
        m = p.matcher(okContent);
        if (m.find()) {
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("</p>");
        m = p.matcher(okContent);

        if (m.find()) {
            okContent = m.replaceAll("");
        }
        p = Pattern.compile("<p>");
        m = p.matcher(okContent);

        if (m.find()) {
            okContent = m.replaceAll("");
        }

        //去除span标签
        p = Pattern.compile("&lt;");
        m = p.matcher(okContent);
        if (m.find()) {
            okContent = m.replaceAll("<");
        }
        p = Pattern.compile("&gt;");
        m = p.matcher(okContent);

        if (m.find()) {
            okContent = m.replaceAll(">");
        }

        return okContent;
    }


    /**
     * 处理正文中的特殊字符
     * @param atricle
     * @return
     */
    static String handlSpecialChar(String atricle){
        p = Pattern.compile("\\$");
        m = p.matcher(atricle);
        if (m.find()) {
            atricle = m.replaceAll("\\\\\\$");
        }

        p = Pattern.compile("\\{");
        m = p.matcher(atricle);
        if (m.find()) {
            atricle = m.replaceAll("\\\\\\{");
        }

        p = Pattern.compile("\\}");
        m = p.matcher(atricle);
        if (m.find()) {
            atricle = m.replaceAll("\\\\\\}");
        }

        return atricle;
    }

    /**
     * 处理文件名中的特殊字符
     * @param title
     * @return
     */
    static String handlSpecialCharForTitle(String title){
        p = Pattern.compile("\\\\");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }

        p = Pattern.compile("\\/");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }

        p = Pattern.compile(":");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }

        p = Pattern.compile("\\*");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }
        p = Pattern.compile("\\?");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }
        p = Pattern.compile("\\|");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }
        p = Pattern.compile("<");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }
        p = Pattern.compile(">");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }
        p = Pattern.compile("\"");
        m = p.matcher(title);
        if (m.find()) {
            title = m.replaceAll("");
        }
        return title;
    }
    /**
     * 获取下一篇文章链接
     * @param div
     * @return
     */
    static String getNextHref(Element div){
        Elements nextArticle = div.getElementsByClass("next_article");
        if(nextArticle.size() == 0){
            return null;
        }
        String nextHref = nextArticle.get(0).child(1).attr("href");

        return nextHref;
    }

    /**
     * 读取模板，生成文章
     * @param title
     * @param date
     * @param okContent
     * @param templatePath
     * @throws Exception
     */
    static void create(String title, String date, String okContent, String template) throws Exception {
//        FileInputStream fileInputStream = new FileInputStream(templatePath);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream, "utf-8"));
//        String line = reader.readLine(); // 读取第一行
//        StringBuffer buffer = new StringBuffer();
//        while (line != null) { // 如果 line 为空说明读完了
//            buffer.append(line); // 将读到的内容添加到 buffer 中
//            buffer.append("\n"); // 添加换行符
//            line = reader.readLine(); // 读取下一行
//        }
//        reader.close();
//        fileInputStream.close();
//
//        String str = new String(buffer.toString().getBytes("UTF-8"));

        String page = replace(template, "title", title);
        page = replace(page, "post_date", date);
        page = replace(page, "content", okContent);


        RandomAccessFile randomFile = null;
        randomFile = new RandomAccessFile("E:\\test.xml", "rw");
        randomFile.seek(randomFile.length()-18);
        randomFile.write(("\n\t"+page+"</channel>\n</rss>").getBytes("UTF-8"));
        randomFile.close();
    }


    static void create1() throws Exception{
        InputStream is = Reptile.class.getClassLoader().getResourceAsStream("template1.xml");

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));

        String line = reader.readLine(); // 读取第一行
        StringBuffer buffer = new StringBuffer();
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();

        String str = new String(buffer.toString().getBytes("UTF-8"));


        RandomAccessFile randomFile = null;
        randomFile = new RandomAccessFile("E:\\test.xml", "rw");
        randomFile.seek(randomFile.length());
        randomFile.write(str.getBytes("UTF-8"));
    }
    /**
     * 替换模板文件中的内容
     * @param str
     * @param name
     * @param content
     * @return
     */
    static String replace(String str, String name, String content) {

        p = Pattern.compile("\\$" + name);
        m = p.matcher(str);

        str = m.replaceAll(content);

        return str;
    }


    public static void main(String[] args) throws Exception {

        String nextHref = "/u283056051/article/details/39755229";

        create1();

        InputStream is = Reptile.class.getClassLoader().getResourceAsStream("template2.xml");

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));

        String line = reader.readLine(); // 读取第一行
        StringBuffer buffer = new StringBuffer();
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n\t"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();

        String str = new String(buffer.toString().getBytes("UTF-8"));

        while (nextHref != null){
            Element div = getContentElement(nextHref);

            String title = getTitle(div);
            System.out.println("************正在下载："+title+"************");
            String date = getPostDate(div);

            String atricle = getAtricleContent(div);//正文内容

            String okContent = replaceRedundancyElement(atricle);

            okContent = handlSpecialChar(okContent);

            nextHref = getNextHref(div); //下一篇文章链接
            if(getArticlType(div)){
                title = "[转]" + title;
            }

            String template = str;
            create(title,date,okContent,str);

            System.out.println("************下载完成："+title+"************");
            System.out.println();
        }

    }


}
