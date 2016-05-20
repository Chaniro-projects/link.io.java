package link.io.java;

import link.io.java.model.ConnectionListener;

public class LinkIOSetup {
    private static LinkIOSetup instance;

    public static LinkIOSetup getInstance() {
        if(instance == null)
            instance = new LinkIOSetup();
        return instance;
    }

    private LinkIOImp linkIO;
    private String serverIP;
    private String mail;
    private String password;
    private String api_key;

    private LinkIOSetup() {
        linkIO = new LinkIOImp();
        serverIP = "";
        mail = "";
        password = "";
        api_key = "";
    }

    public LinkIOSetup connectTo(String serverIP)
    {
        this.serverIP = serverIP;
        return this;
    }

    public LinkIOSetup withMail(String mail)
    {
        this.mail = mail;
        return this;
    }

    public LinkIOSetup withPassword(String password)
    {
        this.password = password;
        return this;
    }

    public LinkIOSetup withAPIKey(String api_key)
    {
        this.api_key = api_key;
        return this;
    }

    public void connect(ConnectionListener listener)
    {
        linkIO.setServerIP(serverIP);
        linkIO.setMail(mail);
        linkIO.setPassword(password);
        linkIO.setAPIKey(api_key);
        linkIO.connect(listener);
    }
}
