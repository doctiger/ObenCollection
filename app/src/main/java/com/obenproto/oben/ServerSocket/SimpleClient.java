package com.obenproto.oben.ServerSocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleClient implements Runnable {
    @Override
    public void run() {

        Socket socket = null;
        try {

            Thread.sleep(5000);

            socket = new Socket("localhost", 3333);

            PrintWriter outWriter = new PrintWriter(
                    socket.getOutputStream(), true);

            outWriter.println("Hello Mr. Server!");

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }
}
