import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//通过socket实现http server服务
public class RawHTTPServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8001);
        while (true) {
            //Thread-->Sleep-->Other Threads
            Socket socket = serverSocket.accept();
            System.out.println("A socket created");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            StringBuilder requestBuilder = new StringBuilder();
            String line="";
            //readline读到line end就是结束，例如'\n',如果读不到readline也会阻塞
            while (true) {
                line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    break;
                }
                requestBuilder.append(line+'\n');
            }
            String request = requestBuilder.toString();
            //打印客户端的内容
            System.out.println(request);
            //返回客户端
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //第一个\n是换行，第二个\n是协议头和body之间需要换行符
            bufferedWriter.write("HTTP/1.1 200 ok\n\nHello World\n");
            //写进socket
            bufferedWriter.flush();
            socket.close();
        }
    }
}
