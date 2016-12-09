package com.test.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * client
 */
public class Client {
	public static void main(String[] args){
		Socket client = null;
		try {
			client = new Socket("localhost", 8888);
			ClientSend cs = new ClientSend(client);
			System.out.println("please input your username：");
			cs.send();
			new Thread(cs).start();//send message
			new Thread(new ClientReceive(client)).start();//receive message
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
//			CloseUtils.closeAll(client);
		}
	}
}

/**
 * receive msg
 */
class ClientReceive implements Runnable{
	private DataInputStream dis;
	private boolean isRunning = true;
	public ClientReceive(){}
	public ClientReceive(Socket client){
		try {
			dis = new DataInputStream(client.getInputStream());
		} catch (IOException e) {
			isRunning = false;
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(isRunning){
			try {
				String msg = dis.readUTF();
				System.out.println(msg);
			} catch (IOException e) {
				isRunning = false;
				e.printStackTrace();
				try {
					dis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}

/**
 * send msg
 */
class ClientSend implements Runnable{
	private DataOutputStream dos;
	private BufferedReader br;
	private boolean isRunning = true;
	public ClientSend(){}
	public ClientSend(Socket client){
		try {
			dos = new DataOutputStream(client.getOutputStream());
			br = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
		} catch (IOException e) {
			isRunning = false;
			e.printStackTrace();
		}
	}
	
	public void send(){
		try {
			String msg = br.readLine();
			System.out.println(msg);
			dos.writeUTF(msg);
			dos.flush();
		} catch (IOException e) {
			isRunning = false;
			e.printStackTrace();
			try {
				dos.close();
				br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void run() {
		while(isRunning){
			send();
		}
	}
}
