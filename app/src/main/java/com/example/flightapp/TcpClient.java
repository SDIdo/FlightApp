//package com.example.flightapp;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.InputStreamReader;
//import java.net.Socket;
//
//class TCPClient implements Runnable {
//    /**
//     * When an object implementing interface <code>Runnable</code> is used
//     * to create a thread, starting the thread causes the object's
//     * <code>run</code> method to be called in that separately executing
//     * thread.
//     * <p>
//     * The general contract of the method <code>run</code> is that it may
//     * take any action whatsoever.
//     *
//     * @see Thread#run()
//     */
//    @Override
//    public void run() {
//            String sentence;
//            String modifiedSentence;
//            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); //TODO from joystick
//            Socket clientSocket = new Socket("localhost", 6789);
//            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            sentence = inFromUser.readLine();
//            outToServer.writeBytes(sentence + 'n');
//            modifiedSentence = inFromServer.readLine();
//            System.out.println("FROM SERVER: " + modifiedSentence);
//            clientSocket.close();
//        }
//    }
//}
