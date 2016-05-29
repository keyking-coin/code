package com.keyking;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.keyking.um.console.ConsoleService;
import com.keyking.util.Instances;
import com.keyking.util.ServerLog;
import com.keyking.util.XmlUtils;

public class AppService implements Instances{
	
	public static String URL;
	
	private static int PORT;
		
	private static int CONSOLE;
		
	public static void main(String[] args) {
        try {
        	ServerLog.init();
        	load();
        	ServerLog.info("DB init");
        	DB.init();
        	PK.load();
        	ServerLog.info("SMS init");
        	SMS.init();
        	CTRL.load();
			HTTP.run(URL,PORT);
			ConsoleService.addConsole(CONSOLE);
		} catch (Exception e) {
			e.printStackTrace();
			ServerLog.error("start fail besause of " + e.getMessage());
			System.exit(0);
		}
	}
	
	public static void load() throws Exception{
		File file = new File("conf/service.xml");
		Document document = XmlUtils.load(file);
		Element element   = document.getDocumentElement();
		URL = XmlUtils.getAttribute(element,"url");
		Element service = XmlUtils.getChildByName(element,"service");
		if (service != null){
			PORT      = Integer.parseInt(XmlUtils.getAttribute(service,"port"));
			CONSOLE   = Integer.parseInt(XmlUtils.getAttribute(service,"console"));
		}
	}
}
 
 
 
