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
            while (!(line = bufferedReader.readLine()).isEmpty()) {
                requestBuilder.append(line);
            }
            String request = requestBuilder.toString();
            System.out.println(request);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedWriter.write("HTTP/1.1 200 ok \n\n Hello World");
            bufferedWriter.flush();
            socket.close();
        }
    }
}
