package com.neuri.trinidad.fitnesserunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.neuri.trinidad.InMemoryTestImpl;
import com.neuri.trinidad.Test;
import com.neuri.trinidad.TestRepository;
import com.neuri.trinidad.TestResultRepository;

import fitnesse.ComponentFactory;
import fitnesse.FitNesseContext;
import fitnesse.authentication.PromiscuousAuthenticator;
import fitnesse.html.HtmlPage;
import fitnesse.html.HtmlPageFactory;
import fitnesse.html.HtmlUtil;
import fitnesse.html.SetupTeardownIncluder;
import fitnesse.responders.ResponderFactory;
import fitnesse.responders.run.SuiteResponder;
import fitnesse.revisioncontrol.RevisionController;
import fitnesse.wiki.FileSystemPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.VirtualEnabledPageCrawler;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;


public class FitNesseRepository implements TestRepository {
	private FitNesseContext context;
	private String fitnesseRoot;
	public FitNesseRepository () {
	}
	public void setUri(String uri) throws IOException {
		context=makeContext(uri);
		fitnesseRoot=uri;
	}
	public FitNesseRepository (String rootDir) throws IOException{
		setUri(rootDir);
	}
	public void prepareResultRepository(TestResultRepository resultRepository)
			throws IOException {
		File cssDir=new File(new File(new File(new File(fitnesseRoot),"FitNesseRoot"),"files"),"css");
		resultRepository.addFile(new File(cssDir,"fitnesse_base.css"), "fitnesse.css");
	}
	public List<Test> getSuite(String name) throws IOException{
		try{
			List<Test> tests=new ArrayList<Test>();
		    WikiPagePath path = PathParser.parse(name);
		    PageCrawler crawler = context.root.getPageCrawler();
		    crawler.setDeadEndStrategy(new VirtualEnabledPageCrawler());
		    WikiPage suiteRoot= crawler.getPage(context.root, path);
		    if (!suiteRoot.getData().hasAttribute("Suite")){
		    	throw new IllegalArgumentException("page "+name+" is not a suite");
		    }
		    WikiPage root=crawler.getPage(context.root,PathParser.parse("."));
		    List<WikiPage> pages=SuiteResponder.makePageList(suiteRoot,root,null);
		    for (WikiPage p:pages){
		    	if (p.getData().hasAttribute("Test")){		    		
				    String testName=crawler.getFullPath(p).toString();
					String content=formatWikiPage(testName, p);
				    tests.add(new InMemoryTestImpl(testName,content));
		    	}
		    }
		    return tests;
		} 
		catch (Exception e){
			throw new IOException("error reading suite "+name ,e);
		}

	}
	public Test getTest(String name)  throws IOException{
		try{
		 WikiPagePath path = PathParser.parse(name);
		 PageCrawler crawler = context.root.getPageCrawler();
		 WikiPage page = crawler.getPage(context.root, path);
		 String content=formatWikiPage(name, page);
		 return new InMemoryTestImpl(name,content);
		}
		catch (Exception ex){
			ex.printStackTrace();
			throw new IOException("Error reading test "+name+ " "+ ex);
		}
	}
	private String formatWikiPage(String name, WikiPage page) throws Exception{
		 PageData pd=page.getData();
		 SetupTeardownIncluder.includeInto(pd);
	     HtmlPage html = context.htmlPageFactory.newPage();
	     html.title.use(name);
	     html.header.use(name);
	     html.main.use(HtmlUtil.addHeaderAndFooter(page, pd.getHtml()));
		 return html.html().replace("href=\"/files/css/", "href=\"");
	}
	private FitNesseContext makeContext(String rootPath) throws IOException {
		try{
	    FitNesseContext context = new FitNesseContext();
	    context.port = 0;
	    context.rootPath = rootPath;
	    ComponentFactory componentFactory = new ComponentFactory(context.rootPath);
	    context.rootPageName = "FitNesseRoot"; //arguments.getRootDirectory();
	    context.rootPagePath = context.rootPath + "/" + context.rootPageName;
	    String defaultNewPageContent = componentFactory.getProperty(ComponentFactory.DEFAULT_NEWPAGE_CONTENT);
	    if (defaultNewPageContent != null)
	      context.defaultNewPageContent = defaultNewPageContent;
	    RevisionController revisioner = componentFactory.loadRevisionController();
	    context.root = componentFactory.getRootPage(FileSystemPage.makeRoot(context.rootPath, context.rootPageName, revisioner));
	    context.responderFactory = new ResponderFactory(context.rootPagePath);
	    context.logger = null;
	    context.authenticator = new PromiscuousAuthenticator();
	    context.htmlPageFactory = componentFactory.getHtmlPageFactory(new HtmlPageFactory());
	    return context;
		}
		catch (Exception e){
			throw new IOException(rootPath +" is not a fitnesse root url", e);
		}
	  }

}
