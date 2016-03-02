package com.obenproto.oben.ServerSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SimpleServer implements Runnable {
    @Override
    public void run() {

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(3333);
            serverSocket.setSoTimeout(20000);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    System.out.println("Client said :"+ inputReader.readLine());

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
