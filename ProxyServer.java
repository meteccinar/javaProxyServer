import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

public class ProxyServer extends Thread {

	final static ArrayList<String> forbiddenAddresses = new ArrayList<>();
	final static HashMap<String,String> cachedAddresses = new HashMap<>();
	final static HashMap<String,String> cacheInformations = new HashMap<>();
    final static ArrayList<String> strTotal = new ArrayList<>();

	static boolean flag = true;
	static boolean wait = true;
	static boolean forMenu;

    public static void main(String[] args)
    {
    	ProxyServer x = new ProxyServer();
    	JFrame frame = new JFrame("METCNR |-| Network Proxy Application");
        JLabel label = new JLabel("Proxy Server not running.", JLabel.CENTER) ;
        JFrame frame2 = new JFrame("Reports");
    	JMenuBar menubar = new JMenuBar();
    	JMenu m1 = new JMenu("File");
    	JMenu m2 = new JMenu("Help");
    	JMenuItem m1_1 = new JMenuItem("Start");
    	JMenuItem m1_2 = new JMenuItem("Stop");
    	JMenuItem m1_3 = new JMenuItem("Report");
    	JMenuItem m1_4 = new JMenuItem("Add host to filter");
    	JMenuItem m1_5 = new JMenuItem("Display current filtered host");
    	JMenuItem m1_6 = new JMenuItem("Exit");
        JTable j;

        String[] columnNames = { "Date", "Client IP", "Requested Domain","Resource Path ","Method" };




    	menubar.add(m1);
    	menubar.add(m2);


    	m1.add(m1_1);
    	m1.add(m1_2);
    	m1.add(m1_3);
    	m1.add(m1_4);
    	m1.add(m1_5);
    	m1.add(m1_6);

    	frame.getContentPane().add(BorderLayout.NORTH,menubar);
    	m1_1.addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			label.setText("Proxy Server running !");
    			SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	wait = true;
                    	forMenu = true;
                    	x.run();


                        return null;
                    }



                };
                worker.execute();



    		}
    	});
    	m1_2.addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			label.setText("Proxy Server stopped !");
    			SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                    	wait = false;
                    	forMenu = false;
                        return null;
                    }



                };
                worker.execute();




    		}
    	});
      	m1_3.addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			JFrame frame5 = new JFrame("Reports");
    			String[] columnNames = {"Date","Client IP","Requested Domain","Path","Method"};
    	        DefaultTableModel model = new DefaultTableModel();
    	        String data;
    	        JTable j = new JTable(model);


    	        model.addColumn("Date");
    	        model.addColumn("Requested Domain");
    	        model.addColumn("Method");
    	        model.addColumn("Client IP");
    	        model.addColumn("Path");
    	        model.setColumnIdentifiers(columnNames);
    	        JScrollPane sp = new JScrollPane(j);
    	        for (int j1 = 0; j1<strTotal.size(); j1++)
    	        {
        	        int i = 0;

    	            data = strTotal.get(j1);
    	            String parts[] = data.split("\\|");

    	            model.addRow(new Object[] {parts[0],parts[1],parts[2],parts[3],parts[4]});





         		}
    	        FileWriter file = null;
				try {
					file = new FileWriter("report.txt");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    	        PrintWriter output = new PrintWriter(file);


    	        for (int row = 0; row < j.getRowCount(); row++) {
    	            for (int col = 0; col < j.getColumnCount(); col++) {
    	            	output.print(j.getColumnName(col));
    	            	output.print(": ");
    	            	output.println(j.getValueAt(row, col));
    	            }
	            	output.println("----------------------");

    	        }

    	        output.close();
    	        frame5.add(sp);
    	        frame5.setSize(400,400);
    	        frame5.setVisible(true);



    		}
    	});

      	m1_4.addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    			String name=JOptionPane.showInputDialog(frame,"Enter a web-adress for forbidding");
    			if(name.contains("https://"))
    			{
    				if(!name.contains("www"))
    				{
    					String newStr = name.substring(8);
            			forbiddenAddresses.add("www."+newStr);
    				}
    				String newStr = name.substring(8);
        			forbiddenAddresses.add(newStr);

    			}
    			else if(name.contains("http://"))
    			{
    				if(!name.contains("www"))
    				{
    					String newStr = name.substring(7);
            			forbiddenAddresses.add("www."+newStr);
    				}
    				String newStr = name.substring(7);
        			forbiddenAddresses.add(newStr);
    			}
    			else
    			{
    				if(!name.contains("www"))
    				{

            			forbiddenAddresses.add("www."+name);
    				}
    				else
    				{
        				forbiddenAddresses.add(name);

    				}
    			}

    			 JOptionPane.showMessageDialog(frame,"Successfully Updated.","Alert",JOptionPane.WARNING_MESSAGE);

    		}
    	});

     	m1_5.addActionListener((ActionListener) new ActionListener() {
    		public void actionPerformed(ActionEvent e)
    		{
    				if(forbiddenAddresses.size()< 1)
    				{
    	    			 JOptionPane.showMessageDialog(frame,"List is Empty !","Alert",JOptionPane.WARNING_MESSAGE);

    				}
    				else
    				{
    					JFrame tmpFrame = new JFrame("Forbidden Address List");
            	        String[] columnNames = {"Forbidden Address"};
            	        DefaultTableModel model = new DefaultTableModel();
            	        String data;
            	        JTable j = new JTable(model);
            	        model.addColumn("Forbidden Address");
            	        for(int i = 0; i < forbiddenAddresses.size(); i++)
            	        {
            	            data = forbiddenAddresses.get(i);

            	        	model.insertRow(0, new Object[] {data});
            	        }
            	        JScrollPane sp = new JScrollPane(j);
            	        tmpFrame.add(sp);
            	        tmpFrame.setSize(400,400);
            	        tmpFrame.setVisible(true);
    				}


    		}
    	});
    	m1_6.addActionListener(e -> System.exit(1));



    	frame.add(label, JLabel.CENTER);

    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(600,600);
    	frame.setVisible(true);




    }

    public ProxyServer() {
        super("Server Thread");



    }


    public boolean checkStatus()
    {
    	return flag;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            Socket socket;
            try {
            	while ((socket = serverSocket.accept()) != null) {
            			(new Handler(socket)).start();
            		}


            } catch (IOException e) {
                e.printStackTrace();  // TODO: implement catch
            }
        } catch (IOException e) {
            e.printStackTrace();  // TODO: implement catch
            return;
        }
    }

    public static class Handler extends Thread {
        public static final Pattern CONNECT_PATTERN = Pattern.compile("CONNECT (.+):(.+) HTTP/(1\\.[01])",Pattern.CASE_INSENSITIVE);
        BufferedReader inFromClient;
    	DataOutputStream outToClient;
    	String host;
    	String path;
		static HashMap<String,String> tmpReport = new HashMap<>();

		private static String strTotal;
        Path file = Paths.get("report");

        PrintWriter writer;
        public static final Pattern GET_PATTERN = Pattern.compile("GET (.+) HTTP/(1\\.[01])",
                Pattern.CASE_INSENSITIVE);


        private Socket clientSocket;
        private Socket client;
        private boolean previousWasR = false;

    	String url;

    	String hd;

    	String constructedHD = "";

    	String reqHeaderRemainingLines;
        public Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            this.client = clientSocket;
        	strTotal = "";




        }

        @Override
        public void run() {
            if(wait)
            {
            	try {
            		  /*for (int i = 0; i < ProxyServer.forbiddenAddresses.size();i++)
                      {
            	          System.out.println("forbiddens : "+ProxyServer.forbiddenAddresses.get(i));
            	      	}   */
            		writer = new PrintWriter(file.toString()+".txt", "UTF-8");


                    String request = readData(clientSocket);
                    System.out.println(request);
                    Matcher matcher = CONNECT_PATTERN.matcher(request);
                    Matcher matcher2 = GET_PATTERN.matcher(request);


                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream(),"ISO-8859-1");
                    OutputStreamWriter outputStreamWriter2 = new OutputStreamWriter(client.getOutputStream(),"ISO-8859-1");

                    DataOutputStream o;
                    if (matcher.matches())
                    {
                    	String header;
                     	do {
                     		header = readData(clientSocket);
                        	System.out.println(header);

                     	} while (!"".equals(header));


                    	if(!ProxyServer.forbiddenAddresses.contains(matcher.group(1)))
                    	{

                        	System.out.println("\nSending CONNECT Method to "+matcher.group(1)+"\n");

                        	final Socket forwardSocket;
                             try {
                                 forwardSocket = new Socket(matcher.group(1), Integer.parseInt(matcher.group(2)));
                                 System.out.println("Destination : "+forwardSocket);
                                 Date d = new Date();
                         		 TimeZone gmt = TimeZone.getTimeZone("GMT");
                         		 SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");
                         		 sdf.setTimeZone(gmt);
                         		 String sdf_date = sdf.format(d);
                         		 String clientAddr = clientSocket.getLocalAddress().toString().substring(1);
                         		 String domain = matcher.group(1);
                         		 String path = "?";
                         		 String method = "CONNECT";
                         		 strTotal = strTotal+sdf_date+"|"+clientAddr+"|"+domain+"|"+path+"|"+method+"|"+"\n";
                         		 ProxyServer.strTotal.add(strTotal);

                             } catch (IOException | NumberFormatException e) {
                                 e.printStackTrace();  // TODO: implement catch
                                 outputStreamWriter.write("HTTP/" + matcher.group(3) + " 502 Bad Gateway\r\n");
                                 outputStreamWriter.write("Proxy-agent: Simple/0.1\r\n");
                                 outputStreamWriter.write("\r\n");
                                 outputStreamWriter.flush();
                                 return;
                             }
                             try {
                                 outputStreamWriter.write("HTTP/" + matcher.group(3) + " 200 Connection established\r\n");
                                 outputStreamWriter.write("Proxy-agent: Simple/0.1\r\n");
                                 outputStreamWriter.write("\r\n");
                                 outputStreamWriter.flush();

                                 Thread remoteToClient = new Thread() {
                                     @Override
                                     public void run() {
                                     	sendData(forwardSocket, clientSocket);
                                     }
                                 };
                                 remoteToClient.start();
                                 try {
                                     if (previousWasR) {
                                         int read = clientSocket.getInputStream().read();
                                         if (read != -1) {
                                             if (read != '\n') {
                                                 forwardSocket.getOutputStream().write(read);
                                             }
                                             sendData(clientSocket, forwardSocket);
                                         } else {
                                             if (!forwardSocket.isOutputShutdown()) {
                                                 forwardSocket.shutdownOutput();
                                             }
                                             if (!clientSocket.isInputShutdown()) {
                                                 clientSocket.shutdownInput();
                                             }
                                         }
                                     } else {
                                     	sendData(clientSocket, forwardSocket);
                                     }
                                 } finally {
                                     try {
                                         remoteToClient.join();
                                     } catch (InterruptedException e) {
                                         e.printStackTrace();  // TODO: implement catch
                                     }
                                 }
                             } finally {
                                 forwardSocket.close();
                                 clientSocket.close();
                             }
                    	}
                    	else
                    	{
                    		byte[] html_page = new byte[1024];
                    		String code= "<!DOCTYPE html>\r\n" +
                                    "<body>\r\n" +
                                    "<h1>\r\n" +
                                    401 +" "+ "Unauthorized� "+"\r\n"+
                                    "</h1>\r\n" +
                                    "Error when fetching URL: "+matcher.group(1)+"\r\n" +
                                    "</body>\r\n" +
                                    "</html>";
                    		html_page = code.getBytes();
                       		outputStreamWriter.write(createErrorPage(401, "Unauthorized",matcher.group(1)));
                    		outputStreamWriter.flush();

                    		System.out.println("The address is forbidden !");


                    	}


                    }


                    if(matcher2.matches()) // FOR GET METHOD CHECK PATTERN
                    {

                    	try {
                            byte[] buffer = new byte[8192];
                            int count = 0;

                            InputStream inFromClient = client.getInputStream();
                            count = inFromClient.read(buffer);
                            String request1 = new String(buffer, 0, count);
                            String host = extractHost(request1);
                            Socket sv = new Socket(host, 80);
                            OutputStream outToHost = sv.getOutputStream();
                            outToHost.write(buffer, 0, count);
                            outToHost.flush();
                            InputStream inFromHost = sv.getInputStream();
                            OutputStream outToClient = client.getOutputStream();
                            while (true) {
                                count = inFromHost.read(buffer);
                                if (count < 0)
                                    break;
                                outToClient.write(buffer, 0, count);
                                outToClient.flush();

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    else
                    {
                    	byte[] html_page = new byte[1024];
                		String code= "<!DOCTYPE html>\r\n" +
                                "<body>\r\n" +
                                "<h1>\r\n" +
                                405 +" "+ "Method Not Allowed� "+"\r\n"+
                                "</h1>\r\n" +
                                "Error when fetching URL"+"\r\n" +
                                "</body>\r\n" +
                                "</html>";
                		html_page = code.getBytes();
                   		outputStreamWriter.write(createErrorPage(405, "Method Not Allowed",""));
                		outputStreamWriter.flush();

                		System.out.println("The address is forbidden !");
                    }


            } catch (IOException e) {
                e.printStackTrace();  // TODO: implement catch
            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: implement catch
                }
            }
            }

        }

        private String extractHost(String request) {
            int start = request.indexOf("Host: ") + 6;
            int end = request.indexOf('\n', start);
            String host = request.substring(start, end - 1);
            return host;
        }

        private static void sendData(Socket inputSocket, Socket outputSocket) {
            try {
                InputStream inputStream = inputSocket.getInputStream();
                try {
                    OutputStream outputStream = outputSocket.getOutputStream();
                    try {
                        byte[] buffer = new byte[4096];
                        int read;
                        do {
                            read = inputStream.read(buffer);
                            if (read > 0) {
                                outputStream.write(buffer, 0, read);

                                if (inputStream.available() < 1) {
                                    outputStream.flush();
                                }
                            }
                        } while (read >= 0);
                    } finally {
                        if (!outputSocket.isOutputShutdown()) {
                            outputSocket.shutdownOutput();
                        }
                    }
                } finally {
                    if (!inputSocket.isInputShutdown()) {
                        inputSocket.shutdownInput();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();  // TODO: implement catch
            }
        }

        private String readData(Socket socket) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int next;
            readerLoop:
            while ((next = socket.getInputStream().read()) != -1) {
                if (previousWasR && next == '\n') {
                    previousWasR = false;
                    continue;
                }
                previousWasR = false;
                switch (next) {
                    case '\r':
                        previousWasR = true;
                        break readerLoop;
                    case '\n':
                        break readerLoop;
                    default:
                        byteArrayOutputStream.write(next);
                        break;
                }
            }
            return byteArrayOutputStream.toString("ISO-8859-1");
        }





        private String createErrorPage(int code, String msg, String address) {
    		String html_page = "<!DOCTYPE html>\r\n" +
                    "<body>\r\n" +
                    "<h1>\r\n" +
                    code +" "+ msg+"\r\n"+
                    "</h1>\r\n" +
                    "Error when fetching URL: "+address+"\r\n" +
                    "</body>\r\n" +
                    "</html>";
    		MimeHeader mh = makeMimeHeader("text/html", html_page.length());
    		HttpResponse hr = new HttpResponse(code, msg, mh);
    		return hr + html_page;
    	}

        private MimeHeader makeMimeHeader(String type, int length) {
    		MimeHeader mh = new MimeHeader();
    		Date d = new Date();
    		TimeZone gmt = TimeZone.getTimeZone("GMT");
    		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss zzz");
    		sdf.setTimeZone(gmt);
    		String sdf_date = sdf.format(d);
    		mh.put("Date", sdf_date);
    		mh.put("Server","CSE 471");
    		mh.put("Content-Type",type);

    		if (length >= 0)
    			mh.put("Content-Length", String.valueOf(length));
    		return mh;
    	}


    }
}
