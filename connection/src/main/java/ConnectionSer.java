import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionSer {

    private FileReader fileReader;
    private ConsoleReader consoleReader;

    public ConnectionSer(FileReader fileReader)  {
        this.fileReader = fileReader;
        this.consoleReader = new ConsoleReader();
    }


    public void createConnection() {


        try {
            JSch jsch = new JSch();

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            String user = "ubuntu";
            String host = "193.40.33.53";
            int port = 22;
            // enter here you private key path
            String privateKey = "./src/main/resources/private.ppk";

            jsch.addIdentity(privateKey);
            System.out.println("identity added ");

            Session session = jsch.getSession(user, host, port);
            session.setConfig(config);
            System.out.println("session created.");

            session.connect();
            System.out.println("session connected.....");

            Channel channel = session.openChannel("sftp");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect();
            System.out.println("shell channel connected....");

            ChannelSftp c = (ChannelSftp) channel;


            c.get("./andmed/tunniplaan_veebruar2018.csv", "./src/com/unitime/project/file.csv");
            fileReader.read("./src/com/unitime/project/file.csv");
            c.exit();
            System.out.println("done");
            session.disconnect();
        } catch (Exception e) {
            System.err.println(e);
        }
    }




    public void createConnection(ConsoleReader consoleReader) {

        int lport = 4256;
        String rhost;
        int rport;

        try{
            JSch jsch=new JSch();

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            String user = "ubuntu";
            String host = "193.40.33.53";
            int port = 22;
            String privateKey = consoleReader.readAndReturnPath();

            jsch.addIdentity(privateKey);
            System.out.println("identity added ");

            Session session = jsch.getSession(user, host, port);
            session.setConfig(config);
            System.out.println("session created.");

            session.connect();

            //Channel channel=session.openChannel("shell");
            //channel.connect();
            rhost = host;
            rport = 1433;

            session.setPortForwardingL(lport, rhost, rport);

            String url = "jdbc:sqlite://localhost:4256/Tunniplaan";
            Connection conn = null;
            conn = DriverManager.getConnection(url);

            System.out.println("done");
            session.disconnect();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }


    public FileReader getFileReader() {
        return fileReader;
    }

    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }
}
