package com.bgm_matome.clawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.Dependent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

@Dependent
public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        Pattern FILTERS2 = Pattern.compile("http://matome.naver.jp/odai/[0-9]{19}(?!/)$");
        Pattern FILTERS3 = Pattern.compile("http://matome.naver.jp/odai/[0-9]{19}\\?page");

        return !FILTERS.matcher(href).matches()
                && (FILTERS2.matcher(href).matches()
                || FILTERS3.matcher(href).matches());
    }

    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();

        if (url.startsWith("http://matome.naver.jp/topic/")) {
            return;
        }

        if (page.getParseData() instanceof HtmlParseData) {

            System.out.println(url);

            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();

            // TODO YoutubeURLを抽出
            String regex = "v=([-\\w]{11})[&\\?]?\"";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(html);
            List<String> list = new ArrayList<>();
            while (m.find()) {
                list.add(m.group(1));
                //System.out.println(m.group(1));
            }

            Set<String> set = new HashSet<String>();
            set.addAll(list);
            List<String> uniqueList = new ArrayList<String>();
            uniqueList.addAll(set);

            for (String u : uniqueList) {
                System.out.println(u);
            }

            // ページング対応
            Document document = Jsoup.parse(html);
            Elements elements = document.select(".MdPagination03");
            Element element = elements.get(0);
            List<Node> nodeList = element.childNodes();
            int size = 0;
            List<Integer> el = new ArrayList<Integer>();
            for (int i = 0; i < nodeList.size(); i++) {
                //for (Node node : nodeList) {
                Node node = nodeList.get(i);
                if (node instanceof Element) {
                    el.add(i);
                }
            }

            if (el.size() <= 1) {
                return;
            }

            Element e = (Element) nodeList.get(el.get(el.size() - 1));
            int last = Integer.parseInt(e.text().trim());

            if (last > 1) {
                for (int i = 2; i <= last; i++) {
                    try {
                        String urlWithPageNo = url + "?&page=" + i;
                        System.out.println(urlWithPageNo);
                        Document d = Jsoup.connect(urlWithPageNo).get();

                        p = Pattern.compile(regex);
                        m = p.matcher(d.html());
                        List<String> list2 = new ArrayList<>();
                        while (m.find()) {
                            list2.add(m.group(1));
                            //System.out.println(m.group(1));
                        }

                        Set<String> set2 = new HashSet<String>();
                        set2.addAll(list2);
                        List<String> uniqueList2 = new ArrayList<String>();
                        uniqueList2.addAll(set2);

                        for (String u : uniqueList2) {
                            System.out.println(u);
                        }

                        // TODO YoutubeURLを抽出
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }
    }
}
