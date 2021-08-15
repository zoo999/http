import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.Function;

/**
 *Date:8/14/2021
 *Author:Zoo
 *Description:优化线程
*/
public class Step2Server {
    ServerSocket serverSocket;
    Function<String, String> handler;

    public Step2Server(Function<String, String> handler) {
        this.handler = handler;
    }


    //Pending Queue
    public void listen(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            this.accept(serverSocket);
        }
    }

    void accept(ServerSocket serverSocket) throws IOException {
        //Blocking..
        Socket socket = serverSocket.accept();
        new Thread(() -> {
            try {
                this.handler(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    void handler(Socket socket) throws IOException {
        //Thread-->Sleep-->Other Threads
        try {
            System.out.println("A socket created");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            StringBuilder requestBuilder = new StringBuilder();
            String line = "";
            //readline读到line end就是结束，例如'\n',如果读不到readline也会阻塞
            while (true) {
                line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                requestBuilder.append(line + '\n');
            }
            String request = requestBuilder.toString();
            //打印客户端的内容
            System.out.println(request);
            //返回客户端
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String response = this.handler.apply(request);

            //第一个\n是换行，第二个\n是协议头和body之间需要换行符
            bufferedWriter.write(response);
            //写进socket
            bufferedWriter.flush();
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Step2Server Step2Server = new Step2Server(req -> {
            try {
                //把处理线程停止掉
                //线程消耗内存比较大的，程序停掉了JVM还没有回收
                //所以用New IO
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "HTTP/1.1 201 ok\n\nGood!\n";
        });
        Step2Server.listen(8001);
    }
}
